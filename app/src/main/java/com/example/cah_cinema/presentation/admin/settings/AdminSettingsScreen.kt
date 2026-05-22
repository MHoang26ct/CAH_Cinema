package com.example.cah_cinema.presentation.admin.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.Holiday
import com.example.cah_cinema.data.model.PriceConfig
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.admin.components.AdminTextField
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun AdminSettingsScreen(
    viewModel: AdminSettingsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddHolidayDialog by remember { mutableStateOf(false) }
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

    AdminSettingsContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onDeleteHoliday = { viewModel.deleteHoliday(it) },
        onAddHolidayClick = { showAddHolidayDialog = true }
    )

    if (showAddHolidayDialog) {
        AddHolidayDialog(
            onDismiss = { showAddHolidayDialog = false },
            onConfirm = { date, name, isRecurring ->
                viewModel.createHoliday(date, name, isRecurring)
                showAddHolidayDialog = false
            }
        )
    }
}

@Composable
fun AdminSettingsContent(
    state: AdminSettingsState,
    snackbarHostState: SnackbarHostState,
    onDeleteHoliday: (Long) -> Unit,
    onAddHolidayClick: () -> Unit = {}
) {
    AdminScaffold(
        title = "Cài đặt hệ thống",
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyanBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // SECTION: CẤU HÌNH GIÁ
                item {
                    Text(
                        text = "Cấu hình giá vé",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        PriceConfigTable(state.priceConfigs)
                    }
                }

                // SECTION: QUẢN LÝ NGÀY LỄ
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Danh sách ngày lễ",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = onAddHolidayClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("THÊM MỚI", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (state.holidays.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Chưa có ngày lễ nào", color = Color.White.copy(alpha = 0.5f))
                        }
                    }
                } else {
                    items(state.holidays) { holiday ->
                        HolidayRow(holiday, onDeleteHoliday)
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun HolidayRow(holiday: Holiday, onDelete: (Long) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1C1C22).copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(holiday.name ?: "Không tên", color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(holiday.date ?: "", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
            }
            if (holiday.isRecurring) {
                Surface(
                    color = CyanBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        "HÀNG NĂM",
                        color = CyanBlue,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            IconButton(onClick = { holiday.id?.let { onDelete(it) } }) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun PriceConfigTable(configs: List<PriceConfig>) {
    Column(
        modifier = Modifier
            .widthIn(min = 600.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1C1C22).copy(alpha = 0.5f))
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1C1C22))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("LOẠI NGÀY", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.width(150.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("KHUNG GIỜ", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.width(150.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("ĐỊNH DẠNG", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.width(120.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("HỆ SỐ", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.width(80.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("THAO TÁC", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }

        configs.forEach { config ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(config.dayType ?: "", color = Color.White, modifier = Modifier.width(150.dp), style = MaterialTheme.typography.bodyMedium)
                Text(config.timeSlot ?: "", color = Color.White, modifier = Modifier.width(150.dp), style = MaterialTheme.typography.bodyMedium)
                Text(config.movieFormat ?: "", color = Color.White, modifier = Modifier.width(120.dp), style = MaterialTheme.typography.bodyMedium)
                Text("x${config.multiplier ?: 1.0}", color = CyanBlue, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                
                Box(modifier = Modifier.weight(1f)) {
                    IconButton(onClick = { }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = CyanBlue, modifier = Modifier.size(18.dp))
                    }
                }
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
        }
    }
}

@Composable
fun AddHolidayDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Boolean) -> Unit
) {
    var day by remember { mutableStateOf("01") }
    var month by remember { mutableStateOf("01") }
    var year by remember { mutableStateOf("2026") }
    var name by remember { mutableStateOf("") }
    var isRecurring by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm ngày lễ mới", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminTextField(value = name, onValueChange = { name = it }, label = "Tên ngày lễ")
                
                Text("Ngày", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(value = day, onValueChange = { if (it.length <= 2) day = it }, label = "Ngày", modifier = Modifier.weight(1f))
                    AdminTextField(value = month, onValueChange = { if (it.length <= 2) month = it }, label = "Tháng", modifier = Modifier.weight(1f))
                    AdminTextField(value = year, onValueChange = { if (it.length <= 4) year = it }, label = "Năm", modifier = Modifier.weight(1.5f))
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it },
                        colors = CheckboxDefaults.colors(checkedColor = CyanBlue)
                    )
                    Text("Lặp lại hàng năm", color = Color.White, fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val date = "$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}"
                    onConfirm(date, name, isRecurring)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = name.isNotBlank()
            ) {
                Text("XÁC NHẬN", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY", color = Color.White.copy(alpha = 0.6f)) }
        },
        containerColor = Color(0xFF21212B)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminSettingsPreview() {
    CAH_CinemaTheme {
        AdminSettingsContent(
            state = AdminSettingsState(
                priceConfigs = listOf(
                    PriceConfig(id = 1, multiplier = 1.0, dayType = "WEEKDAY", timeSlot = "MORNING", movieFormat = "2D"),
                    PriceConfig(id = 2, multiplier = 1.2, dayType = "WEEKDAY", timeSlot = "EVENING", movieFormat = "2D"),
                    PriceConfig(id = 3, multiplier = 1.5, dayType = "WEEKEND", timeSlot = "EVENING", movieFormat = "3D"),
                    PriceConfig(id = 4, multiplier = 2.0, dayType = "HOLIDAY", timeSlot = "EVENING", movieFormat = "IMAX")
                ),
                holidays = listOf(
                    Holiday(id = 1, date = "2026-01-01", name = "Tết Dương lịch", isRecurring = true),
                    Holiday(id = 2, date = "2026-04-30", name = "Ngày Giải phóng miền Nam", isRecurring = true),
                    Holiday(id = 3, date = "2026-09-02", name = "Quốc khánh", isRecurring = true)
                ),
                isLoading = false
            ),
            snackbarHostState = SnackbarHostState(),
            onDeleteHoliday = {},
            onAddHolidayClick = {}
        )
    }
}
