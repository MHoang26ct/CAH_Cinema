package com.example.cah_cinema.presentation.admin.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    
    var showEditConfigDialog by remember { mutableStateOf<PriceConfig?>(null) }
    var showAddHolidayDialog by remember { mutableStateOf(false) }

    AdminSettingsContent(
        state = state,
        onDeleteHoliday = { viewModel.deleteHoliday(it) },
        onEditConfig = { showEditConfigDialog = it },
        onAddHolidayClick = { showAddHolidayDialog = true }
    )

    if (showEditConfigDialog != null) {
        EditPriceConfigDialog(
            config = showEditConfigDialog!!,
            onDismiss = { showEditConfigDialog = null },
            onConfirm = { multiplier ->
                viewModel.updatePriceConfig(showEditConfigDialog!!.copy(multiplier = multiplier))
                showEditConfigDialog = null
            }
        )
    }

    if (showAddHolidayDialog) {
        AddHolidayDialog(
            onDismiss = { showAddHolidayDialog = false },
            onConfirm = { date, name, recurring ->
                viewModel.createHoliday(date, name, recurring)
                showAddHolidayDialog = false
            }
        )
    }
}

@Composable
fun AdminSettingsContent(
    state: AdminSettingsState,
    onDeleteHoliday: (Long) -> Unit,
    onEditConfig: (PriceConfig) -> Unit,
    onAddHolidayClick: () -> Unit
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
                    PriceConfigTable(state.priceConfigs, onEditConfig)
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
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("THÊM MỚI", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
fun EditPriceConfigDialog(
    config: PriceConfig,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var multiplier by remember { mutableStateOf(config.multiplier.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chỉnh sửa cấu hình giá", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Cấu hình: ${config.dayType} - ${config.timeSlot} (${config.movieFormat})", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                OutlinedTextField(
                    value = multiplier, 
                    onValueChange = { multiplier = it }, 
                    label = { Text("Hệ số nhân (Multiplier)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = CyanBlue,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedBorderColor = CyanBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(multiplier.toDoubleOrNull() ?: 1.0) }, colors = ButtonDefaults.buttonColors(containerColor = CyanBlue)) {
                Text("CẬP NHẬT", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY") }
        },
        containerColor = Color(0xFF21212B)
    )
}

@Composable
fun AddHolidayDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-01-01") }
    var recurring by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm ngày lễ mới", color = Color.White) },
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
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                    )
                )
                OutlinedTextField(
                    value = date, 
                    onValueChange = { date = it }, 
                    label = { Text("Ngày (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = CyanBlue,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedBorderColor = CyanBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = recurring, onCheckedChange = { recurring = it })
                    Text("Lặp lại hàng năm", color = Color.White)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(date, name, recurring) }, colors = ButtonDefaults.buttonColors(containerColor = CyanBlue)) {
                Text("XÁC NHẬN", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY") }
        },
        containerColor = Color(0xFF21212B)
    )
}

@Composable
fun PriceConfigTable(configs: List<PriceConfig>, onEdit: (PriceConfig) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C22), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Text("Loại ngày", color = CyanBlue, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Khung giờ", color = CyanBlue, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Định dạng", color = CyanBlue, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Hệ số", color = CyanBlue, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(48.dp))
        }
        configs.forEach { config ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(config.dayType, color = Color.White, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodyMedium)
                Text(config.timeSlot, color = Color.White, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodyMedium)
                Text(config.movieFormat, color = Color.White, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Text("x${config.multiplier}", color = CyanBlue, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                IconButton(onClick = { onEdit(config) }, modifier = Modifier.size(32.dp)) {
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
                            Text("LẶP LẠI", color = CyanBlue, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
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
