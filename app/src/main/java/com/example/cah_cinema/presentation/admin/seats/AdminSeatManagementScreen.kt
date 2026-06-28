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
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
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

// ─── Row label helper ───────────────────────────────────────────────────────
fun rowIndexToLabel(seatRowIndex: Int): String {
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
    cinemaId: Long = 0L,
    onBack: () -> Unit
) {
    val viewModel: AdminSeatManagementViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val savedStateHandle = androidx.lifecycle.SavedStateHandle(
                    mapOf("roomId" to roomId, "cinemaId" to cinemaId)
                )
                return AdminSeatManagementViewModel(savedStateHandle) as T
            }
        }
    )
    val state by viewModel.state.collectAsState()
    var selectedTypeId by remember { mutableStateOf(TYPE_REGULAR) }
    var showConfigDialog by remember { mutableStateOf(false) }
    var showCancelShowtimesDialog by remember { mutableStateOf(false) }
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

    AdminSeatManagementContent(
        state = state,
        selectedTypeId = selectedTypeId,
        onTypeSelected = { selectedTypeId = it },
        onCellClick = { gr, gc -> viewModel.onCellClick(gr, gc, selectedTypeId) },
        onSave = { viewModel.saveSeatMap(onBack) },
        onClearAll = { viewModel.clearAll() },
        onBack = onBack,
        onShowConfig = { showConfigDialog = true },
        onShowCancelShowtimes = { showCancelShowtimesDialog = true },
        totalGridRows = viewModel.totalGridRows(),
        totalGridCols = viewModel.totalGridCols(),
        snackbarHostState = snackbarHostState
    )

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

    if (showCancelShowtimesDialog) {
        CancelShowtimesDialog(
            onDismiss = { showCancelShowtimesDialog = false },
            onConfirm = { fromDate, toDate, reason ->
                viewModel.cancelShowtimesByRoom(fromDate, toDate, reason) {
                    showCancelShowtimesDialog = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSeatManagementContent(
    state: AdminSeatMapState,
    selectedTypeId: Long,
    onTypeSelected: (Long) -> Unit,
    onCellClick: (Int, Int) -> Unit,
    onSave: () -> Unit,
    onClearAll: () -> Unit,
    onBack: () -> Unit,
    onShowConfig: () -> Unit,
    onShowCancelShowtimes: () -> Unit = {},
    totalGridRows: Int,
    totalGridCols: Int,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        containerColor = Color(0xFF13131A),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("THIẾT KẾ SƠ ĐỒ GHẾ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CyanBlue, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    IconButton(onClick = onShowConfig, enabled = !state.isLoading) {
                        Icon(Icons.Default.Settings, contentDescription = "Cấu hình", tint = Color.White.copy(alpha = 0.7f))
                    }
                    IconButton(onClick = onShowCancelShowtimes, enabled = !state.isLoading && !state.isSaving) {
                        Icon(Icons.Default.DateRange, contentDescription = "Hủy suất chiếu", tint = Color(0xFFFF9800).copy(alpha = 0.8f))
                    }
                    IconButton(onClick = onClearAll, enabled = !state.isLoading) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa tất cả", tint = Color.Red.copy(alpha = 0.7f))
                    }
                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !state.isSaving && !state.isLoading
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
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
        if (state.isLoading && state.cells.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyanBlue)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                SeatTypeSelectorBar(selectedTypeId = selectedTypeId, onTypeSelected = onTypeSelected)
                SeatCountSummary(cells = state.cells.values.toList())
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).height(6.dp).background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(3.dp)))
                Text("MÀN HÌNH", color = Color.White.copy(alpha = 0.3f), fontSize = 10.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp)) {
                    val hScroll = rememberScrollState()
                    val vScroll = rememberScrollState()
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(vScroll).horizontalScroll(hScroll)) {
                        SeatGridColHeader(totalGridCols = totalGridCols, cells = state.cells)
                        for (gr in 0 until totalGridRows) {
                            SeatGridRow(gridRow = gr, totalGridCols = totalGridCols, cells = state.cells, onCellClick = { gc -> onCellClick(gr, gc) })
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                SeatLegendBar()
            }
        }
    }
}

@Composable
fun SeatTypeSelectorBar(selectedTypeId: Long, onTypeSelected: (Long) -> Unit) {
    val types = listOf(TYPE_REGULAR to "THƯỜNG", TYPE_VIP to "VIP", TYPE_COUPLE to "ĐÔI", TYPE_AISLE to "LỐI ĐI", TYPE_EMPTY to "XÓA")
    Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF1C1C22)).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        types.forEach { (typeId, label) ->
            val isSelected = selectedTypeId == typeId
            val color = if (typeId == TYPE_EMPTY) Color(0xFF455A64) else seatTypeColor(typeId)
            Surface(
                modifier = Modifier.clickable { onTypeSelected(typeId) }.border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) CyanBlue else Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)),
                color = if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(label, color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SeatCountSummary(cells: List<GridCell>) {
    val regular = cells.count { it.typeId == TYPE_REGULAR }; val vip = cells.count { it.typeId == TYPE_VIP }
    val couple = cells.count { it.typeId == TYPE_COUPLE }; val total = regular + vip + couple
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SeatCountChip("Tổng: $total", Color.White.copy(alpha = 0.7f))
        SeatCountChip("Thường: $regular", COLOR_REGULAR)
        SeatCountChip("VIP: $vip", COLOR_VIP)
        SeatCountChip("Đôi: $couple", COLOR_COUPLE)
    }
}

@Composable
fun SeatCountChip(text: String, color: Color) {
    Text(text = text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
}

val SEAT_SIZE = 34.dp
val AISLE_SIZE = 14.dp

@Composable
fun SeatGridColHeader(totalGridCols: Int, cells: Map<Pair<Int, Int>, GridCell>) {
    Row(modifier = Modifier.padding(start = 36.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        var seatColIdx = 0
        for (gc in 0 until totalGridCols) {
            val cell = cells[0 to gc]
            if (cell?.isAisleCol == true) Spacer(modifier = Modifier.width(AISLE_SIZE))
            else {
                seatColIdx++
                Box(modifier = Modifier.size(SEAT_SIZE), contentAlignment = Alignment.Center) {
                    Text(text = "$seatColIdx", color = Color.White.copy(alpha = 0.3f), fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun SeatGridRow(gridRow: Int, totalGridCols: Int, cells: Map<Pair<Int, Int>, GridCell>, onCellClick: (Int) -> Unit) {
    val firstCell = cells[gridRow to 0]
    if (firstCell?.isAisleRow == true) { Spacer(modifier = Modifier.height(AISLE_SIZE)); return }
    val seatRowIndex = firstCell?.backendRow?.toInt() ?: 0
    Row(modifier = Modifier.padding(bottom = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(32.dp), contentAlignment = Alignment.Center) {
            Text(text = rowIndexToLabel(seatRowIndex), color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        for (gc in 0 until totalGridCols) {
            val cell = cells[gridRow to gc]
            if (cell?.isAisleCol == true) Spacer(modifier = Modifier.width(AISLE_SIZE))
            else SeatDesignCell(cell = cell ?: GridCell(gridRow, gc, 0.0, 0.0), onClick = { onCellClick(gc) })
        }
    }
}

@Composable
fun SeatDesignCell(cell: GridCell, onClick: () -> Unit) {
    val bgColor = if (cell.typeId == TYPE_EMPTY) Color.White.copy(alpha = 0.05f) else seatTypeColor(cell.typeId).copy(alpha = 0.85f)
    val borderColor = if (cell.typeId == TYPE_EMPTY) Color.White.copy(alpha = 0.08f) else seatTypeColor(cell.typeId)
    Box(modifier = Modifier.size(SEAT_SIZE).clip(RoundedCornerShape(4.dp)).background(bgColor).border(1.dp, borderColor, RoundedCornerShape(4.dp)).clickable { onClick() }, contentAlignment = Alignment.Center) {
        if (cell.typeId != TYPE_EMPTY) {
            Text(text = when (cell.typeId) { TYPE_VIP -> "V"; TYPE_COUPLE -> "♥"; TYPE_AISLE -> "—"; else -> "" }, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SeatLegendBar() {
    val items = listOf(TYPE_REGULAR to "Ghế thường", TYPE_VIP to "Ghế VIP", TYPE_COUPLE to "Ghế đôi", TYPE_AISLE to "Lối đi", TYPE_EMPTY to "Trống")
    Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF1C1C22)).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEach { (typeId, label) ->
            val color = if (typeId == TYPE_EMPTY) Color(0xFF455A64) else seatTypeColor(typeId)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(6.dp)); Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun GridConfigDialog(currentSeatRows: Int, currentSeatCols: Int, currentAisleAfterRows: Set<Int>, currentAisleAfterCols: Set<Int>, onDismiss: () -> Unit, onConfirm: (Int, Int, Set<Int>, Set<Int>) -> Unit) {
    var seatRows by remember { mutableStateOf(currentSeatRows.toString()) }
    var seatCols by remember { mutableStateOf(currentSeatCols.toString()) }
    var aisleRowsText by remember { mutableStateOf(currentAisleAfterRows.sorted().joinToString(",")) }
    var aisleColsText by remember { mutableStateOf(currentAisleAfterCols.sorted().joinToString(",")) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF21212B)) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Cấu hình sơ đồ ghế", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ConfigTextField(value = seatRows, onValueChange = { seatRows = it }, label = "Số hàng ghế", modifier = Modifier.weight(1f))
                    ConfigTextField(value = seatCols, onValueChange = { seatCols = it }, label = "Số cột ghế", modifier = Modifier.weight(1f))
                }
                ConfigTextField(value = aisleRowsText, onValueChange = { aisleRowsText = it }, label = "Lối đi ngang sau hàng")
                ConfigTextField(value = aisleColsText, onValueChange = { aisleColsText = it }, label = "Lối đi dọc sau cột")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("HỦY", color = Color.White.copy(alpha = 0.5f)) }
                    Button(onClick = {
                        val rows = seatRows.toIntOrNull() ?: currentSeatRows; val cols = seatCols.toIntOrNull() ?: currentSeatCols
                        val aisleRows = aisleRowsText.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
                        val aisleCols = aisleColsText.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
                        onConfirm(rows, cols, aisleRows, aisleCols)
                    }, colors = ButtonDefaults.buttonColors(containerColor = CyanBlue)) { Text("ÁP DỤNG", color = Color.Black, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
fun ConfigTextField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label, fontSize = 11.sp) }, modifier = modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedLabelColor = CyanBlue, unfocusedLabelColor = Color.White.copy(alpha = 0.5f), focusedBorderColor = CyanBlue, unfocusedBorderColor = Color.White.copy(alpha = 0.2f)))
}

@Composable
fun CancelShowtimesDialog(
    onDismiss: () -> Unit,
    onConfirm: (fromDate: String, toDate: String, reason: String) -> Unit
) {
    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF21212B)) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Hủy suất chiếu theo phòng", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "Dùng khi phòng cần bảo trì/sửa sơ đồ ghế. Hệ thống sẽ refund booking đã thanh toán.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                ConfigTextField(value = fromDate, onValueChange = { fromDate = it }, label = "Từ ngày (yyyy-MM-dd)")
                ConfigTextField(value = toDate, onValueChange = { toDate = it }, label = "Đến ngày (yyyy-MM-dd)")
                ConfigTextField(value = reason, onValueChange = { reason = it }, label = "Lý do hủy")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("HỦY", color = Color.White.copy(alpha = 0.5f)) }
                    Button(
                        onClick = { onConfirm(fromDate.trim(), toDate.trim(), reason.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        enabled = fromDate.isNotBlank() && toDate.isNotBlank()
                    ) {
                        Text("XÁC NHẬN", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
