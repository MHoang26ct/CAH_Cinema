package com.example.cah_cinema.presentation.admin.seats

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.CancelShowtimesByRoomRequest
import com.example.cah_cinema.data.model.CreateSeatRequest
import com.example.cah_cinema.data.model.ReplaceSeatMapRequest
import com.example.cah_cinema.data.model.SeatItem
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class AdminSeatManagementViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val roomId: Long = savedStateHandle["roomId"] ?: 0L
    private val cinemaId: Long = savedStateHandle["cinemaId"] ?: 0L

    private val _state = MutableStateFlow(AdminSeatMapState(roomId = roomId))
    val state: StateFlow<AdminSeatMapState> = _state.asStateFlow()

    // Map: UI typeId (TYPE_REGULAR/VIP/COUPLE/AISLE) → real DB seatTypeId
    // Populated từ response API, không hardcode
    private val seatTypeMapping = mutableMapOf<Long, Long>()

    init {
        loadExistingSeats()
    }

    /**
     * Resolve UI typeId sang real backend seatTypeId.
     * Nếu chưa có mapping thì fallback về chính UI typeId
     * (trường hợp DB seed đúng theo thứ tự 1,2,3,4).
     */
    private fun resolveBackendTypeId(uiTypeId: Long): Long =
        seatTypeMapping[uiTypeId] ?: uiTypeId

    private fun loadExistingSeats() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                Log.d("AdminSeatMap", "Loading existing seats for roomId $roomId...")
                val response = repository.getSeatsByRoom(roomId)
                Log.d("AdminSeatMap", "Load response: code=${response?.code}, dataSize=${response?.data?.size}")

                if (response?.code in 200..299 && response?.data != null) {
                    val seats = response.data
                    if (seats.isNotEmpty()) {
                        // Extract typeName → seatTypeId mapping từ chính phòng này
                        extractSeatTypeMapping(seats)
                        applyLoadedSeats(seats)
                    } else {
                        // Phòng mới chưa có ghế — cần bootstrap mapping từ phòng khác
                        loadSeatTypeMappingFromOtherRoom()
                        buildGrid()
                        _state.update { it.copy(isLoading = false, hasExistingSeatMap = false) }
                    }
                } else {
                    loadSeatTypeMappingFromOtherRoom()
                    buildGrid()
                    _state.update { it.copy(isLoading = false, hasExistingSeatMap = false) }
                }
            } catch (e: Exception) {
                Log.e("AdminSeatMap", "Error loading seats", e)
                loadSeatTypeMappingFromOtherRoom()
                buildGrid()
                _state.update { it.copy(isLoading = false, hasExistingSeatMap = false) }
            }
        }
    }

    /**
     * Khi phòng chưa có ghế, lấy seat type mapping từ phòng đầu tiên trong cùng rạp.
     * Nếu không có phòng nào có ghế, thử tất cả rạp để tìm phòng có ghế.
     */
    private suspend fun loadSeatTypeMappingFromOtherRoom() {
        if (seatTypeMapping.isNotEmpty()) return  // đã có rồi

        try {
            // Thử lấy danh sách phòng của cùng rạp
            val rooms = if (cinemaId > 0) {
                repository.getRoomsByCinema(cinemaId)?.data ?: emptyList()
            } else emptyList()

            val otherRooms = rooms.filter { it.id != roomId }

            for (room in otherRooms) {
                val seatsResponse = repository.getSeatsByRoom(room.id)
                val seats = seatsResponse?.data ?: emptyList()
                if (seats.isNotEmpty()) {
                    extractSeatTypeMapping(seats)
                    Log.d("AdminSeatMap", "Loaded seat type mapping from roomId=${room.id}: $seatTypeMapping")
                    return
                }
            }

            // Nếu không tìm được trong cùng rạp, thử rạp khác
            if (seatTypeMapping.isEmpty()) {
                val allCinemas = repository.getCinemas()?.data ?: emptyList()
                outer@ for (cinema in allCinemas) {
                    val cinemaRooms = repository.getRoomsByCinema(cinema.id)?.data ?: emptyList()
                    for (room in cinemaRooms) {
                        val seatsResponse = repository.getSeatsByRoom(room.id)
                        val seats = seatsResponse?.data ?: emptyList()
                        if (seats.isNotEmpty()) {
                            extractSeatTypeMapping(seats)
                            Log.d("AdminSeatMap", "Loaded seat type mapping from cinema=${cinema.id} room=${room.id}: $seatTypeMapping")
                            break@outer
                        }
                    }
                }
            }

            if (seatTypeMapping.isEmpty()) {
                Log.w("AdminSeatMap", "Could not resolve seat type mapping — will use UI typeId as fallback")
            }
        } catch (e: Exception) {
            Log.e("AdminSeatMap", "Error loading seat type mapping", e)
        }
    }

    /**
     * Parse danh sách SeatItem và build map: UI_TYPE_ID → DB seatTypeId
     * Dựa vào typeName của từng ghế.
     */
    private fun extractSeatTypeMapping(seats: List<SeatItem>) {
        seats.forEach { item ->
            val seatType = item.seatType ?: return@forEach
            val dbId = seatType.seatTypeId
            val uiId = when (seatType.typeName.uppercase()) {
                "NORMAL", "REGULAR" -> TYPE_REGULAR
                "VIP"               -> TYPE_VIP
                "COUPLE"            -> TYPE_COUPLE
                "AISLE"             -> TYPE_AISLE
                else                -> return@forEach
            }
            seatTypeMapping[uiId] = dbId
        }
    }

    private fun applyLoadedSeats(seatItems: List<SeatItem>) {
        if (seatItems.isEmpty()) {
            buildGrid()
            _state.update { it.copy(hasExistingSeatMap = false, isLoading = false) }
            return
        }

        val realSeats = seatItems.filter { it.seatType?.typeName != "AISLE" }
        // Tọa độ backend = seatIndex + 1 (do offset +1 khi save).
        // Nên seatRows = maxBackendRow - 1, seatCols = maxBackendCol - 1
        val maxBackendRow = realSeats.maxOfOrNull { it.row }?.toInt() ?: 9
        val maxBackendCol = realSeats.maxOfOrNull { it.col }?.toInt() ?: 11
        val maxRow = (maxBackendRow - 1).coerceAtLeast(1)
        val maxCol = (maxBackendCol - 1).coerceAtLeast(1)

        _state.update {
            it.copy(
                seatRows = maxRow,
                seatCols = maxCol,
                aisleAfterRows = emptySet(),
                aisleAfterCols = emptySet(),
                hasExistingSeatMap = true,
                cells = emptyMap()
            )
        }
        buildGrid()

        val newCells = _state.value.cells.toMutableMap()
        seatItems.forEach { item ->
            val typeId = mapSeatTypeName(item.seatType?.typeName ?: "REGULAR")
            val cellEntry = newCells.entries.find {
                abs(it.value.backendRow - item.row) < 0.01 &&
                    abs(it.value.backendCol - item.col) < 0.01
            }
            if (cellEntry != null) {
                newCells[cellEntry.key] = cellEntry.value.copy(typeId = typeId)
            } else {
                Log.w("AdminSeatMap", "Seat at ${item.row},${item.col} not found in grid config")
            }
        }
        _state.update { it.copy(cells = newCells, isLoading = false) }
    }

    private fun mapSeatTypeName(typeName: String): Long = when (typeName) {
        "VIP" -> TYPE_VIP
        "COUPLE" -> TYPE_COUPLE
        "AISLE" -> TYPE_AISLE
        else -> TYPE_REGULAR
    }

    private fun buildGrid() {
        val s = _state.value
        val cells = mutableMapOf<Pair<Int, Int>, GridCell>()

        val totalGridRows = s.seatRows + s.aisleAfterRows.size
        val totalGridCols = s.seatCols + s.aisleAfterCols.size

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

    private fun buildCoordMap(seatCount: Int, aisleAfter: Set<Int>): Map<Int, Double> {
        // Backend yêu cầu row/col > 1.0 (DecimalMin inclusive=false),
        // nên offset tất cả tọa độ thêm 1: ghế thứ 1 → 2.0, thứ 2 → 3.0, ...
        val map = mutableMapOf<Int, Double>()
        var gridIdx = 0
        var seatIdx = 1
        while (seatIdx <= seatCount) {
            map[gridIdx] = (seatIdx + 1).toDouble()   // +1 để thoả > 1.0
            gridIdx++
            if (aisleAfter.contains(seatIdx) && seatIdx < seatCount) {
                map[gridIdx] = seatIdx + 1.5           // aisle: x.5, cũng đã offset
                gridIdx++
            }
            seatIdx++
        }
        return map
    }

    fun totalGridRows(): Int {
        val s = _state.value
        return s.seatRows + s.aisleAfterRows.size
    }

    fun totalGridCols(): Int {
        val s = _state.value
        return s.seatCols + s.aisleAfterCols.size
    }

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

    fun updateGridConfig(
        seatRows: Int,
        seatCols: Int,
        aisleAfterRows: Set<Int>,
        aisleAfterCols: Set<Int>
    ) {
        _state.update {
            it.copy(
                seatRows = seatRows,
                seatCols = seatCols,
                aisleAfterRows = aisleAfterRows,
                aisleAfterCols = aisleAfterCols,
                cells = emptyMap()
            )
        }
        buildGrid()
    }

    fun clearAll() {
        _state.update { s ->
            val newCells = s.cells.mapValues { (_, cell) -> cell.copy(typeId = TYPE_EMPTY) }
            s.copy(cells = newCells)
        }
    }

    /**
     * Lưu sơ đồ ghế:
     * - Chưa có sơ đồ: POST /admin/seats/create
     * - Đã có sơ đồ: PUT /admin/seats/replace (room cloning — UI vẫn như edit)
     */
    fun saveSeatMap(onSuccess: () -> Unit) {
        val s = _state.value
        val seatCells = s.cells.values
            .filter { it.isSeat && it.typeId != TYPE_AISLE }
            .sortedWith(compareBy({ it.backendRow }, { it.backendCol }))

        Log.d("AdminSeatMap", "Preparing to save ${seatCells.size} seats for roomId $roomId")

        if (seatCells.isEmpty()) {
            _state.update { it.copy(errorMessage = "Chưa có ghế nào trong sơ đồ") }
            return
        }

        val coupleSeats = seatCells.filter { it.typeId == TYPE_COUPLE }
        if (coupleSeats.size % 2 != 0) {
            _state.update { it.copy(errorMessage = "Ghế đôi phải đi thành cặp (số lượng lẻ)") }
            return
        }

        val requests = seatCells.map { cell ->
            CreateSeatRequest(
                roomId = roomId,
                row = cell.backendRow,
                col = cell.backendCol,
                seatTypeId = resolveBackendTypeId(cell.typeId)  // map UI typeId → real DB id
            )
        }

        _state.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = if (s.hasExistingSeatMap) {
                    Log.d("AdminSeatMap", "Replacing seat map via PUT /admin/seats/replace...")
                    repository.replaceSeatMap(ReplaceSeatMapRequest(roomId, requests))
                } else {
                    Log.d("AdminSeatMap", "Creating seat map via POST /admin/seats/create...")
                    repository.createSeats(requests)
                }
                Log.d("AdminSeatMap", "Save response: code=${response?.code}, message=${response?.message}")

                if (response?.code in 200..299) {
                    val msg = if (s.hasExistingSeatMap) {
                        "Cập nhật sơ đồ ghế thành công. Suất chiếu sau 7 ngày đã chuyển sang phòng mới."
                    } else {
                        "Lưu sơ đồ ghế thành công!"
                    }
                    _state.update { it.copy(isSaving = false, successMessage = msg) }
                    onSuccess()
                } else {
                    val errorMsg = response?.message ?: "Lỗi lưu sơ đồ (${response?.code})"
                    Log.e("AdminSeatMap", "Save failed: $errorMsg")
                    _state.update { it.copy(isSaving = false, errorMessage = errorMsg) }
                }
            } catch (e: Exception) {
                Log.e("AdminSeatMap", "Exception saving seat map", e)
                _state.update { it.copy(isSaving = false, errorMessage = e.message ?: "Lỗi kết nối") }
            }
        }
    }

    fun cancelShowtimesByRoom(fromDate: String, toDate: String, reason: String, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = repository.cancelShowtimesByRoom(
                    CancelShowtimesByRoomRequest(
                        roomId = roomId,
                        fromDate = fromDate,
                        toDate = toDate,
                        reason = reason.ifBlank { null }
                    )
                )
                if (response?.code in 200..299) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            successMessage = response?.message ?: "Đã hủy suất chiếu thành công"
                        )
                    }
                    onSuccess()
                } else {
                    _state.update {
                        it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi hủy suất chiếu")
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Lỗi kết nối")
                }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
