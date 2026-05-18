package com.example.cah_cinema.presentation.admin.seats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.CreateSeatRequest
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Loại ô trong grid thiết kế sơ đồ ghế.
 * Tọa độ backend dùng x.5 cho aisle, x.0 cho ghế thật.
 *
 * seatTypeId mapping:
 *   1L = REGULAR (ghế thường)
 *   2L = VIP
 *   3L = COUPLE (ghế đôi - phải đặt thành cặp liên tiếp)
 *   4L = AISLE  (lối đi - không gửi lên backend, chỉ dùng để tính tọa độ x.5)
 *   0L = EMPTY  (ô trống, không có ghế)
 */
const val TYPE_EMPTY = 0L
const val TYPE_REGULAR = 1L
const val TYPE_VIP = 2L
const val TYPE_COUPLE = 3L
const val TYPE_AISLE = 4L

/**
 * Mỗi ô trong grid thiết kế.
 * gridRow/gridCol: chỉ số 0-based trong grid hiển thị (bao gồm cả hàng/cột aisle)
 * backendRow/backendCol: tọa độ gửi lên backend (x.0 cho ghế, x.5 cho aisle)
 */
data class GridCell(
    val gridRow: Int,
    val gridCol: Int,
    val backendRow: Double,  // 1.0, 1.5, 2.0, 2.5...
    val backendCol: Double,  // 1.0, 1.5, 2.0, 2.5...
    val typeId: Long = TYPE_EMPTY
) {
    val isAisleRow: Boolean get() = backendRow % 1.0 != 0.0
    val isAisleCol: Boolean get() = backendCol % 1.0 != 0.0
    val isAisle: Boolean get() = isAisleRow || isAisleCol
    val isSeat: Boolean get() = !isAisle && typeId != TYPE_EMPTY
}

data class AdminSeatMapState(
    val roomId: Long = 0,
    // Grid cells: key = (gridRow, gridCol)
    val cells: Map<Pair<Int, Int>, GridCell> = emptyMap(),
    // Kích thước grid (số hàng và cột ghế thật, không tính aisle)
    val seatRows: Int = 8,   // số hàng ghế thật
    val seatCols: Int = 10,  // số cột ghế thật
    val aisleAfterRows: Set<Int> = setOf(3, 6),  // sau hàng ghế thứ mấy thì có aisle ngang
    val aisleAfterCols: Set<Int> = setOf(3, 7),  // sau cột ghế thứ mấy thì có aisle dọc
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AdminSeatManagementViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val roomId: Long = savedStateHandle["roomId"] ?: 0L

    private val _state = MutableStateFlow(AdminSeatMapState(roomId = roomId))
    val state: StateFlow<AdminSeatMapState> = _state.asStateFlow()

    init {
        buildGrid()
    }

    /**
     * Xây dựng grid từ cấu hình hiện tại.
     * Grid bao gồm cả hàng/cột aisle xen kẽ.
     */
    private fun buildGrid() {
        val s = _state.value
        val cells = mutableMapOf<Pair<Int, Int>, GridCell>()

        // Tính tổng số hàng grid (ghế + aisle ngang)
        val totalGridRows = s.seatRows + s.aisleAfterRows.size
        val totalGridCols = s.seatCols + s.aisleAfterCols.size

        // Map: gridRow → backendRow
        val gridRowToBackend = buildCoordMap(s.seatRows, s.aisleAfterRows)
        val gridColToBackend = buildCoordMap(s.seatCols, s.aisleAfterCols)

        for (gr in 0 until totalGridRows) {
            for (gc in 0 until totalGridCols) {
                val br = gridRowToBackend[gr] ?: continue
                val bc = gridColToBackend[gc] ?: continue
                val key = gr to gc
                val existing = s.cells[key]
                cells[key] = GridCell(
                    gridRow = gr,
                    gridCol = gc,
                    backendRow = br,
                    backendCol = bc,
                    typeId = existing?.typeId ?: TYPE_EMPTY
                )
            }
        }
        _state.update { it.copy(cells = cells) }
    }

    /**
     * Tạo map từ gridIndex → backendCoord.
     * Ví dụ: seatCount=8, aisleAfter={3,6}
     *   gridIdx 0→1.0, 1→2.0, 2→3.0, 3→4.0, 4→4.5(aisle), 5→5.0, 6→6.0, 7→7.0, 8→7.5(aisle), 9→8.0
     */
    private fun buildCoordMap(seatCount: Int, aisleAfter: Set<Int>): Map<Int, Double> {
        val map = mutableMapOf<Int, Double>()
        var gridIdx = 0
        var seatIdx = 1
        while (seatIdx <= seatCount) {
            map[gridIdx] = seatIdx.toDouble()
            gridIdx++
            if (aisleAfter.contains(seatIdx) && seatIdx < seatCount) {
                // Thêm aisle sau ghế này
                map[gridIdx] = seatIdx + 0.5
                gridIdx++
            }
            seatIdx++
        }
        return map
    }

    /**
     * Tổng số hàng grid (bao gồm aisle)
     */
    fun totalGridRows(): Int {
        val s = _state.value
        return s.seatRows + s.aisleAfterRows.size
    }

    fun totalGridCols(): Int {
        val s = _state.value
        return s.seatCols + s.aisleAfterCols.size
    }

    /**
     * Nhấn vào ô trong grid:
     * - Nếu là aisle: không làm gì
     * - Nếu đang EMPTY → set type
     * - Nếu đang cùng type → xóa (EMPTY)
     * - Nếu khác type → đổi type
     */
    fun onCellClick(gridRow: Int, gridCol: Int, selectedTypeId: Long) {
        val key = gridRow to gridCol
        _state.update { s ->
            val cell = s.cells[key] ?: return@update s
            if (cell.isAisle) return@update s

            val newTypeId = if (cell.typeId == selectedTypeId) TYPE_EMPTY else selectedTypeId
            val newCells = s.cells.toMutableMap()
            newCells[key] = cell.copy(typeId = newTypeId)
            s.copy(cells = newCells)
        }
    }

    /**
     * Cập nhật cấu hình grid (số hàng, cột, vị trí aisle) và rebuild
     */
    fun updateGridConfig(
        seatRows: Int,
        seatCols: Int,
        aisleAfterRows: Set<Int>,
        aisleAfterCols: Set<Int>
    ) {
        _state.update { it.copy(
            seatRows = seatRows,
            seatCols = seatCols,
            aisleAfterRows = aisleAfterRows,
            aisleAfterCols = aisleAfterCols,
            cells = emptyMap() // reset cells
        ) }
        buildGrid()
    }

    /**
     * Xóa toàn bộ sơ đồ (reset về EMPTY)
     */
    fun clearAll() {
        _state.update { s ->
            val newCells = s.cells.mapValues { (_, cell) -> cell.copy(typeId = TYPE_EMPTY) }
            s.copy(cells = newCells)
        }
    }

    /**
     * Lưu sơ đồ ghế lên backend.
     * Chỉ gửi các ô có typeId != EMPTY và không phải aisle.
     * Ghế đôi phải đi thành cặp liên tiếp (backend validate).
     */
    fun saveSeatMap(onSuccess: () -> Unit) {
        val s = _state.value
        val seatCells = s.cells.values
            .filter { it.isSeat && it.typeId != TYPE_AISLE }
            .sortedWith(compareBy({ it.backendRow }, { it.backendCol }))

        if (seatCells.isEmpty()) {
            _state.update { it.copy(errorMessage = "Chưa có ghế nào trong sơ đồ") }
            return
        }

        // Validate ghế đôi phải đi thành cặp
        val coupleSeats = seatCells.filter { it.typeId == TYPE_COUPLE }
        if (coupleSeats.size % 2 != 0) {
            _state.update { it.copy(errorMessage = "Ghế đôi phải đi thành cặp (số lượng lẻ)") }
            return
        }

        // Build request list - aisle cells gửi với seatTypeId = 4 (AISLE)
        // Nhưng backend không nhận AISLE qua API create, chỉ dùng tọa độ x.5
        // Nên ta chỉ gửi ghế thật (không gửi aisle)
        val requests = seatCells.map { cell ->
            CreateSeatRequest(
                roomId = roomId,
                row = cell.backendRow,
                col = cell.backendCol,
                seatTypeId = cell.typeId
            )
        }

        _state.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                // Xóa sơ đồ cũ trước (ignore error nếu chưa có)
                try { repository.deleteSeatsByRoom(roomId) } catch (_: Exception) {}

                val response = repository.createSeats(requests)
                if (response?.code in 200..299) {
                    _state.update { it.copy(isSaving = false, successMessage = "Lưu sơ đồ ghế thành công!") }
                    onSuccess()
                } else {
                    _state.update { it.copy(isSaving = false, errorMessage = response?.message ?: "Lỗi lưu sơ đồ") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
