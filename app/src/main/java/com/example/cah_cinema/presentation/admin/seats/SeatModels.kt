package com.example.cah_cinema.presentation.admin.seats

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
    val hasExistingSeatMap: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
