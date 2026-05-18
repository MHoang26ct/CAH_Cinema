package com.example.cah_cinema.presentation.admin.seats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.ui.theme.CyanBlue

// ─── Màu sắc loại ghế ───────────────────────────────────────────────────────
val COLOR_REGULAR = Color(0xFF2E7D32)
val COLOR_VIP = Color(0xFFD87D4A)
val COLOR_COUPLE = Color(0xFFC2185B)
val COLOR_AISLE = Color(0xFF37474F)
val COLOR_EMPTY = Color(0xFF1C1C22)

fun seatTypeColor(typeId: Long): Color = when (typeId) {
    TYPE_REGULAR -> COLOR_REGULAR
    TYPE_VIP -> COLOR_VIP
    TYPE_COUPLE -> COLOR_COUPLE
    TYPE_AISLE -> COLOR_AISLE
    else -> COLOR_EMPTY
}

fun seatTypeLabel(typeId: Long): String = when (typeId) {
    TYPE_REGULAR -> "THƯỜNG"
    TYPE_VIP -> "VIP"
    TYPE_COUPLE -> "ĐÔI"
    TYPE_AISLE -> "LỐI ĐI"
    else -> "TRỐNG"
}

// ─── Row label helper (giống SeatLabelUtil backend) ─────────────────────────
fun rowIndexToLabel(seatRowIndex: Int): String {
    // seatRowIndex: 1-based (1→A, 2→B, ...)
    var n = seatRowIndex
    val sb = StringBuilder()
    while (n > 0) {
        n--
        sb.insert(0, ('A' + (n % 26)))
        n /= 26
    }
    return sb.toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSeatManagementScreen(
    roomId: Long,
    onBack: () -> Unit
) {
    val viewModel: AdminSeatManagementViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val savedStateHandle = androidx.lifecycle.SavedStateHandle(mapOf("roomId" to roomId))
                return AdminSeatManagementViewModel(savedStateHandle) as T
            }
        }
    )
    val state by viewModel.state.collectAsState()
    var selectedTypeId by remember { mutableStateOf(TYPE_REGULAR) }
    var showConfigDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = Color(0xFF13131A),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "THIẾT KẾ SƠ ĐỒ GHẾ",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Nút cấu hình grid
                    IconButton(onClick = { showConfigDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Cấu hình", tint = Color.White.copy(alpha = 0.7f))
                    }
                    // Nút xóa tất cả
                    IconButton(onClick = { viewModel.clearAll() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa tất cả", tint = Color.Red.copy(alpha = 0.7f))
                    }
                    // Nút lưu
                    Button(
                        onClick = { viewModel.saveSeatMap(onBack) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !state.isSaving,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("LƯU", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1C1C22))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Toolbar chọn loại ghế ──────────────────────────────────────
            SeatTypeSelectorBar(
                selectedTypeId = selectedTypeId,
                onTypeSelected = { selectedTypeId = it }
            )

            // ── Thống kê nhanh ─────────────────────────────────────────────
            SeatCountSummary(cells = state.cells.values.toList())

            // ── Màn hình (screen indicator) ────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .height(6.dp)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
            )
            Text(
                "MÀN HÌNH",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Grid sơ đồ ghế (scroll ngang + dọc) ───────────────────────
            val totalRows = viewModel.totalGridRows()
            val totalCols = viewModel.totalGridCols()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                val hScroll = rememberScrollState()
                val vScroll = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(vScroll)
                        .horizontalScroll(hScroll)
                ) {
                    // Header cột (số cột ghế thật)
                    SeatGridColHeader(
                        totalGridCols = totalCols,
                        cells = state.cells,
                        seatCols = state.seatCols
                    )

                    // Các hàng ghế
                    for (gr in 0 until totalRows) {
                        SeatGridRow(
                            gridRow = gr,
                            totalGridCols = totalCols,
                            cells = state.cells,
                            selectedTypeId = selectedTypeId,
                            seatRows = state.seatRows,
                            onCellClick = { gc -> viewModel.onCellClick(gr, gc, selectedTypeId) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ── Chú thích ──────────────────────────────────────────────────
            SeatLegendBar()
        }
    }

    // Dialog cấu hình grid
    if (showConfigDialog) {
        GridConfigDialog(
            currentSeatRows = state.seatRows,
            currentSeatCols = state.seatCols,
            currentAisleAfterRows = state.aisleAfterRows,
            currentAisleAfterCols = state.aisleAfterCols,
            onDismiss = { showConfigDialog = false },
            onConfirm = { rows, cols, aisleRows, aisleCols ->
                viewModel.updateGridConfig(rows, cols, aisleRows, aisleCols)
                showConfigDialog = false
            }
        )
    }
}

// ─── Toolbar chọn loại ghế ───────────────────────────────────────────────────
@Composable
fun SeatTypeSelectorBar(selectedTypeId: Long, onTypeSelected: (Long) -> Unit) {
    val types = listOf(
        TYPE_REGULAR to "THƯỜNG",
        TYPE_VIP to "VIP",
        TYPE_COUPLE to "ĐÔI",
        TYPE_AISLE to "LỐI ĐI",
        TYPE_EMPTY to "XÓA"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C22))
            .padding(horizontal = 16.dp, vertical = 12.dp),  // tăng padding cho tablet
        horizontalArrangement = Arrangement.spacedBy(12.dp),  // tăng spacing
        verticalAlignment = Alignment.CenterVertically
    ) {
        types.forEach { (typeId, label) ->
            val isSelected = selectedTypeId == typeId
            val color = if (typeId == TYPE_EMPTY) Color(0xFF455A64) else seatTypeColor(typeId)

            Surface(
                modifier = Modifier
                    .clickable { onTypeSelected(typeId) }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) CyanBlue else Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                color = if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),  // tăng padding
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)  // tăng từ 12 → 14
                            .background(color, RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        label,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp,  // tăng từ 11 → 13
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ─── Thống kê số ghế ─────────────────────────────────────────────────────────
@Composable
fun SeatCountSummary(cells: List<GridCell>) {
    val regular = cells.count { it.typeId == TYPE_REGULAR }
    val vip = cells.count { it.typeId == TYPE_VIP }
    val couple = cells.count { it.typeId == TYPE_COUPLE }
    val aisle = cells.count { it.typeId == TYPE_AISLE }
    val total = regular + vip + couple

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),  // tăng padding
        horizontalArrangement = Arrangement.spacedBy(16.dp)  // tăng spacing
    ) {
        SeatCountChip("Tổng: $total", Color.White.copy(alpha = 0.7f))
        SeatCountChip("Thường: $regular", COLOR_REGULAR)
        SeatCountChip("VIP: $vip", COLOR_VIP)
        SeatCountChip("Đôi: $couple", COLOR_COUPLE)
        SeatCountChip("Lối đi: $aisle", COLOR_AISLE)
    }
}

@Composable
fun SeatCountChip(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 13.sp,  // tăng từ 11 → 13
        fontWeight = FontWeight.Medium
    )
}

// ─── Header cột ──────────────────────────────────────────────────────────────
@Composable
fun SeatGridColHeader(
    totalGridCols: Int,
    cells: Map<Pair<Int, Int>, GridCell>,
    seatCols: Int
) {
    Row(
        modifier = Modifier.padding(start = 36.dp),  // tăng từ 28 → 36 khớp với label width mới
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var seatColIdx = 0
        for (gc in 0 until totalGridCols) {
            val cell = cells[0 to gc]
            val isAisleCol = cell?.isAisleCol ?: false
            if (isAisleCol) {
                Spacer(modifier = Modifier.width(AISLE_SIZE))
            } else {
                seatColIdx++
                Box(
                    modifier = Modifier.size(SEAT_SIZE),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$seatColIdx",
                        color = Color.White.copy(alpha = 0.3f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ─── Một hàng trong grid ─────────────────────────────────────────────────────
// Tăng kích thước ghế cho tablet: 26dp → 34dp để dễ nhấn và nhìn hơn
val SEAT_SIZE = 34.dp
val AISLE_SIZE = 14.dp

@Composable
fun SeatGridRow(
    gridRow: Int,
    totalGridCols: Int,
    cells: Map<Pair<Int, Int>, GridCell>,
    selectedTypeId: Long,
    seatRows: Int,
    onCellClick: (Int) -> Unit
) {
    // Lấy cell đầu tiên của hàng để biết backendRow
    val firstCell = cells[gridRow to 0]
    val isAisleRow = firstCell?.isAisleRow ?: false

    if (isAisleRow) {
        // Hàng aisle ngang: chỉ hiển thị khoảng trống
        Spacer(modifier = Modifier.height(AISLE_SIZE))
        return
    }

    // Tính label hàng (A, B, C...)
    val seatRowIndex = cells.values
        .filter { it.gridRow == gridRow && !it.isAisleRow }
        .minByOrNull { it.backendRow }
        ?.backendRow?.toInt() ?: 0
    val rowLabel = rowIndexToLabel(seatRowIndex)

    Row(
        modifier = Modifier.padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label hàng bên trái
        Box(
            modifier = Modifier.width(32.dp),  // tăng từ 24 → 32
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rowLabel,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp,  // tăng từ 10 → 12
                fontWeight = FontWeight.Bold
            )
        }

        // Các ô trong hàng
        for (gc in 0 until totalGridCols) {
            val cell = cells[gridRow to gc]
            if (cell == null) {
                Spacer(modifier = Modifier.size(SEAT_SIZE))
                continue
            }

            if (cell.isAisleCol) {
                // Lối đi dọc
                Spacer(modifier = Modifier.width(AISLE_SIZE))
            } else {
                SeatDesignCell(
                    cell = cell,
                    onClick = { onCellClick(gc) }
                )
            }
        }
    }
}

// ─── Ô ghế trong grid thiết kế ───────────────────────────────────────────────
@Composable
fun SeatDesignCell(cell: GridCell, onClick: () -> Unit) {
    val bgColor = when {
        cell.typeId == TYPE_EMPTY -> Color.White.copy(alpha = 0.05f)
        else -> seatTypeColor(cell.typeId).copy(alpha = 0.85f)
    }
    val borderColor = when {
        cell.typeId == TYPE_EMPTY -> Color.White.copy(alpha = 0.08f)
        else -> seatTypeColor(cell.typeId)
    }

    Box(
        modifier = Modifier
            .size(SEAT_SIZE)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (cell.typeId != TYPE_EMPTY) {
            Text(
                text = when (cell.typeId) {
                    TYPE_VIP -> "V"
                    TYPE_COUPLE -> "♥"
                    TYPE_AISLE -> "—"
                    else -> ""
                },
                color = Color.White,
                fontSize = 11.sp,  // tăng từ 9 → 11
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─── Chú thích ───────────────────────────────────────────────────────────────
@Composable
fun SeatLegendBar() {
    val items = listOf(
        TYPE_REGULAR to "Ghế thường",
        TYPE_VIP to "Ghế VIP",
        TYPE_COUPLE to "Ghế đôi",
        TYPE_AISLE to "Lối đi",
        TYPE_EMPTY to "Trống"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C22))
            .padding(horizontal = 16.dp, vertical = 12.dp),  // tăng padding
        horizontalArrangement = Arrangement.spacedBy(16.dp),  // tăng spacing
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (typeId, label) ->
            val color = if (typeId == TYPE_EMPTY) Color(0xFF455A64) else seatTypeColor(typeId)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(14.dp)  // tăng từ 12 → 14
                        .background(color, RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)  // tăng từ 10 → 12
            }
        }
    }
}

// ─── Dialog cấu hình grid ────────────────────────────────────────────────────
@Composable
fun GridConfigDialog(
    currentSeatRows: Int,
    currentSeatCols: Int,
    currentAisleAfterRows: Set<Int>,
    currentAisleAfterCols: Set<Int>,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Set<Int>, Set<Int>) -> Unit
) {
    var seatRows by remember { mutableStateOf(currentSeatRows.toString()) }
    var seatCols by remember { mutableStateOf(currentSeatCols.toString()) }
    var aisleRowsText by remember { mutableStateOf(currentAisleAfterRows.sorted().joinToString(",")) }
    var aisleColsText by remember { mutableStateOf(currentAisleAfterCols.sorted().joinToString(",")) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF21212B)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Cấu hình sơ đồ ghế",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    "Nhập số hàng/cột ghế thật (không tính lối đi).\nLối đi: nhập số thứ tự hàng/cột sau đó có lối đi, cách nhau bằng dấu phẩy.\nVí dụ: \"3,6\" = có lối đi sau hàng 3 và hàng 6.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ConfigTextField(
                        value = seatRows,
                        onValueChange = { seatRows = it },
                        label = "Số hàng ghế",
                        modifier = Modifier.weight(1f)
                    )
                    ConfigTextField(
                        value = seatCols,
                        onValueChange = { seatCols = it },
                        label = "Số cột ghế",
                        modifier = Modifier.weight(1f)
                    )
                }

                ConfigTextField(
                    value = aisleRowsText,
                    onValueChange = { aisleRowsText = it },
                    label = "Lối đi ngang sau hàng (VD: 3,6)"
                )

                ConfigTextField(
                    value = aisleColsText,
                    onValueChange = { aisleColsText = it },
                    label = "Lối đi dọc sau cột (VD: 3,7)"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("HỦY", color = Color.White.copy(alpha = 0.5f))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val rows = seatRows.toIntOrNull()?.coerceIn(1, 20) ?: currentSeatRows
                            val cols = seatCols.toIntOrNull()?.coerceIn(1, 20) ?: currentSeatCols
                            val aisleRows = aisleRowsText.split(",")
                                .mapNotNull { it.trim().toIntOrNull() }
                                .filter { it in 1 until rows }
                                .toSet()
                            val aisleCols = aisleColsText.split(",")
                                .mapNotNull { it.trim().toIntOrNull() }
                                .filter { it in 1 until cols }
                                .toSet()
                            onConfirm(rows, cols, aisleRows, aisleCols)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanBlue)
                    ) {
                        Text("ÁP DỤNG", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ConfigTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = CyanBlue,
            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
            focusedBorderColor = CyanBlue,
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
        )
    )
}
