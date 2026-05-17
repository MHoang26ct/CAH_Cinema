package com.example.cah_cinema.presentation.admin.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.Holiday
import com.example.cah_cinema.data.model.PriceConfig
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun AdminSettingsScreen(
    viewModel: AdminSettingsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddHolidayDialog by remember { mutableStateOf(false) }

    AdminSettingsContent(
        state = state,
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
    onDeleteHoliday: (Long) -> Unit,
    onAddHolidayClick: () -> Unit = {}
) {
    AdminScaffold(title = "Cài đặt hệ thống") { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyanBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // SECTION: CẤU HÌNH GIÁ
                item {
                    SectionHeader("Cấu hình giá vé")
                    // Bọc bảng trong horizontalScroll để tránh tràn
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
                        SectionHeader("Danh sách ngày lễ")
                        Button(
                            onClick = onAddHolidayClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("THÊM MỚI", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HolidayList(state.holidays, onDeleteHoliday)
                }
            }
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
        title = { Text("Thêm ngày lễ", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên ngày lễ") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = CyanBlue,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedBorderColor = CyanBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    )
                )
                Text("Ngày", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = { if (it.length <= 2) day = it },
                        label = { Text("Ngày") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = CyanBlue, unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                    OutlinedTextField(
                        value = month,
                        onValueChange = { if (it.length <= 2) month = it },
                        label = { Text("Tháng") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = CyanBlue, unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { if (it.length <= 4) year = it },
                        label = { Text("Năm") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = CyanBlue, unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
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

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun PriceConfigTable(configs: List<PriceConfig>) {
    // Đặt minWidth để tránh tràn — bọc ngoài bằng horizontalScroll
    Column(
        modifier = Modifier
            .widthIn(min = 500.dp)
            .background(Color(0xFF1C1C22), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Text("Loại ngày", color = CyanBlue, modifier = Modifier.width(120.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Khung giờ", color = CyanBlue, modifier = Modifier.width(120.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Định dạng", color = CyanBlue, modifier = Modifier.width(100.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Hệ số", color = CyanBlue, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(48.dp))
        }
        configs.forEach { config ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(config.dayType, color = Color.White, modifier = Modifier.width(120.dp), style = MaterialTheme.typography.bodyMedium)
                Text(config.timeSlot, color = Color.White, modifier = Modifier.width(120.dp), style = MaterialTheme.typography.bodyMedium)
                Text(config.movieFormat, color = Color.White, modifier = Modifier.width(100.dp), style = MaterialTheme.typography.bodyMedium)
                Text("x${config.multiplier}", color = CyanBlue, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                IconButton(onClick = { }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                }
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
        }
    }
}

@Composable
fun HolidayList(holidays: List<Holiday>, onDelete: (Long) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (holidays.isEmpty()) {
            Text(
                text = "Chưa có ngày lễ nào",
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.padding(16.dp)
            )
        }
        holidays.forEach { holiday ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1C1C22),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(holiday.name, color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text(holiday.date, color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
                    }
                    if (holiday.isRecurring) {
                        Surface(
                            color = CyanBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                "LẶP LẠI",
                                color = CyanBlue,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = { holiday.id?.let { onDelete(it) } }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
