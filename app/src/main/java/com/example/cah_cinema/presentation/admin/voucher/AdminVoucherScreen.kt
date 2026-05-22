package com.example.cah_cinema.presentation.admin.voucher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.CreateVoucherRequest
import com.example.cah_cinema.data.model.UpdateVoucherRequest
import com.example.cah_cinema.data.model.VoucherItem
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.admin.components.AdminTextField
import com.example.cah_cinema.presentation.user.booking.formatPrice
import com.example.cah_cinema.util.DateTimeUtils
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun AdminVoucherScreen(
    viewModel: AdminVoucherViewModel = viewModel(),
    onNavigate: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingVoucher by remember { mutableStateOf<VoucherItem?>(null) }

    AdminVoucherContent(
        state = state,
        onNavigate = onNavigate,
        onAddClick = { showAddDialog = true },
        onEditClick = { editingVoucher = it },
        onDeleteVoucher = { viewModel.deleteVoucher(it) },
        onLoadMore = { viewModel.loadVouchers() }
    )

    if (showAddDialog) {
        AddVoucherDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { request ->
                viewModel.createVoucher(request) {
                    showAddDialog = false
                }
            }
        )
    }

    editingVoucher?.let { v ->
        EditVoucherDialog(
            voucher = v,
            onDismiss = { editingVoucher = null },
            onConfirm = { request ->
                viewModel.updateVoucher(request) {
                    editingVoucher = null
                }
            }
        )
    }
}

@Composable
fun AdminVoucherContent(
    state: AdminVoucherState,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (VoucherItem) -> Unit = {},
    onDeleteVoucher: (Long) -> Unit,
    onLoadMore: () -> Unit = {}
) {
    AdminScaffold(
        title = "Voucher & Khuyến mãi"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mã giảm giá đang hoạt động (${state.vouchers.size})",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TẠO VOUCHER", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading && state.vouchers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanBlue)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.vouchers) { v ->
                        VoucherCard(
                            voucher = v, 
                            onEdit = { onEditClick(v) },
                            onDelete = { onDeleteVoucher(v.id) }
                        )
                    }
                    
                    if (!state.isLastPage && state.vouchers.isNotEmpty()) {
                        item {
                            Button(
                                onClick = onLoadMore,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text("Xem thêm", color = CyanBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditVoucherDialog(
    voucher: VoucherItem,
    onDismiss: () -> Unit,
    onConfirm: (UpdateVoucherRequest) -> Unit
) {
    var code by remember { mutableStateOf(voucher.code) }
    var value by remember { mutableStateOf(voucher.value.toString()) }
    var quantity by remember { mutableStateOf(voucher.quantity?.toString() ?: "") }
    var minOrderValue by remember { mutableStateOf(voucher.minOrderValue?.toString() ?: "") }
    var type by remember { mutableStateOf(voucher.type) }
    
    val startIso = voucher.startAt ?: ""
    var startDay by remember { mutableStateOf(if(startIso.length >= 10) startIso.substring(8, 10) else DateTimeUtils.getTodayDay()) }
    var startMonth by remember { mutableStateOf(if(startIso.length >= 10) startIso.substring(5, 7) else DateTimeUtils.getTodayMonth()) }
    var startYear by remember { mutableStateOf(if(startIso.length >= 10) startIso.substring(0, 4) else DateTimeUtils.getTodayYear()) }
    
    val endIso = voucher.expiredAt
    var endDay by remember { mutableStateOf(if(endIso.length >= 10) endIso.substring(8, 10) else "31") }
    var endMonth by remember { mutableStateOf(if(endIso.length >= 10) endIso.substring(5, 7) else "12") }
    var endYear by remember { mutableStateOf(if(endIso.length >= 10) endIso.substring(0, 4) else DateTimeUtils.getTodayYear()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sửa voucher", color = Color.White) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminTextField(value = code, onValueChange = { code = it }, label = "Mã voucher")
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = (type == "FIXED_AMOUNT"), onClick = { type = "FIXED_AMOUNT" })
                    Text("Cố định", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = (type == "PERCENT"), onClick = { type = "PERCENT" })
                    Text("Phần trăm", color = Color.White, fontSize = 12.sp)
                }

                AdminTextField(value = value, onValueChange = { value = it }, label = "Giá trị")
                
                Text("Ngày bắt đầu", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(value = startDay, onValueChange = { startDay = it }, label = "Ngày", modifier = Modifier.weight(1f))
                    AdminTextField(value = startMonth, onValueChange = { startMonth = it }, label = "Tháng", modifier = Modifier.weight(1f))
                    AdminTextField(value = startYear, onValueChange = { startYear = it }, label = "Năm", modifier = Modifier.weight(1.5f))
                }

                Text("Ngày hết hạn", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(value = endDay, onValueChange = { endDay = it }, label = "Ngày", modifier = Modifier.weight(1f))
                    AdminTextField(value = endMonth, onValueChange = { endMonth = it }, label = "Tháng", modifier = Modifier.weight(1f))
                    AdminTextField(value = endYear, onValueChange = { endYear = it }, label = "Năm", modifier = Modifier.weight(1.5f))
                }

                AdminTextField(value = quantity, onValueChange = { quantity = it }, label = "Số lượng")
                AdminTextField(value = minOrderValue, onValueChange = { minOrderValue = it }, label = "Đơn hàng tối thiểu")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startAtStr = "$startYear-${startMonth.padStart(2, '0')}-${startDay.padStart(2, '0')}T00:00:00"
                    val expiredAtStr = "$endYear-${endMonth.padStart(2, '0')}-${endDay.padStart(2, '0')}T23:59:59"
                    onConfirm(
                        UpdateVoucherRequest(
                            voucherId = voucher.id,
                            code = code,
                            type = type,
                            value = value.toDoubleOrNull() ?: 0.0,
                            quantity = quantity.toIntOrNull() ?: 100,
                            startAt = startAtStr,
                            expiredAt = expiredAtStr,
                            isActive = voucher.isActive ?: true,
                            isDeleted = voucher.isDeleted ?: false,
                            minOrderValue = minOrderValue.toDoubleOrNull(),
                            maxDiscount = if (type == "PERCENT") (value.toDoubleOrNull() ?: 0.0) * 1000.0 else null
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue)
            ) {
                Text("LƯU", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY") }
        },
        containerColor = Color(0xFF21212B)
    )
}

@Composable
fun AddVoucherDialog(
    onDismiss: () -> Unit,
    onConfirm: (CreateVoucherRequest) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var minOrderValue by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("FIXED_AMOUNT") }
    
    var startDay by remember { mutableStateOf(DateTimeUtils.getTodayDay()) }
    var startMonth by remember { mutableStateOf(DateTimeUtils.getTodayMonth()) }
    var startYear by remember { mutableStateOf(DateTimeUtils.getTodayYear()) }
    
    var endDay by remember { mutableStateOf("31") }
    var endMonth by remember { mutableStateOf("12") }
    var endYear by remember { mutableStateOf(DateTimeUtils.getTodayYear()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo voucher mới", color = Color.White) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminTextField(value = code, onValueChange = { code = it }, label = "Mã voucher")
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = (type == "FIXED_AMOUNT"), onClick = { type = "FIXED_AMOUNT" })
                    Text("Cố định", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = (type == "PERCENT"), onClick = { type = "PERCENT" })
                    Text("Phần trăm", color = Color.White, fontSize = 12.sp)
                }

                AdminTextField(value = value, onValueChange = { value = it }, label = "Giá trị")
                
                Text("Ngày bắt đầu", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(value = startDay, onValueChange = { if(it.length <= 2) startDay = it }, label = "Ngày", modifier = Modifier.weight(1f))
                    AdminTextField(value = startMonth, onValueChange = { if(it.length <= 2) startMonth = it }, label = "Tháng", modifier = Modifier.weight(1f))
                    AdminTextField(value = startYear, onValueChange = { if(it.length <= 4) startYear = it }, label = "Năm", modifier = Modifier.weight(1.5f))
                }

                Text("Ngày hết hạn", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(value = endDay, onValueChange = { if(it.length <= 2) endDay = it }, label = "Ngày", modifier = Modifier.weight(1f))
                    AdminTextField(value = endMonth, onValueChange = { if(it.length <= 2) endMonth = it }, label = "Tháng", modifier = Modifier.weight(1f))
                    AdminTextField(value = endYear, onValueChange = { if(it.length <= 4) endYear = it }, label = "Năm", modifier = Modifier.weight(1.5f))
                }

                AdminTextField(value = quantity, onValueChange = { quantity = it }, label = "Số lượng")
                AdminTextField(value = minOrderValue, onValueChange = { minOrderValue = it }, label = "Đơn hàng tối thiểu")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startAtStr = "$startYear-${startMonth.padStart(2, '0')}-${startDay.padStart(2, '0')}T00:00:00"
                    val expiredAtStr = "$endYear-${endMonth.padStart(2, '0')}-${endDay.padStart(2, '0')}T23:59:59"
                    onConfirm(
                        CreateVoucherRequest(
                            code = code,
                            type = type,
                            value = value.toDoubleOrNull() ?: 0.0,
                            quantity = quantity.toIntOrNull() ?: 100,
                            startAt = startAtStr,
                            expiredAt = expiredAtStr,
                            minOrderValue = minOrderValue.toDoubleOrNull(),
                            maxDiscount = if (type == "PERCENT") (value.toDoubleOrNull() ?: 0.0) * 1000.0 else null
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue)
            ) {
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
fun VoucherCard(voucher: VoucherItem, onEdit: () -> Unit = {}, onDelete: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1C1C22),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                color = CyanBlue.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(26.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = voucher.code, color = CyanBlue, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(text = if (voucher.type == "PERCENT") "Giảm ${voucher.value}%" else "Giảm ${formatPrice(voucher.value)}", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Đơn tối thiểu: ${formatPrice(voucher.minOrderValue ?: 0.0)}", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
                Text(text = "Hết hạn: ${DateTimeUtils.formatDateTime(voucher.expiredAt)}", color = Color.White.copy(alpha = 0.3f), style = MaterialTheme.typography.labelSmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.4f))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminVoucherPreview() {
    CAH_CinemaTheme {
        AdminVoucherContent(
            state = AdminVoucherState(
                vouchers = listOf(
                    VoucherItem(
                        id = 1L, 
                        code = "SUMMER20", 
                        type = "FIXED_AMOUNT", 
                        value = 20000.0, 
                        minOrderValue = 100000.0, 
                        maxDiscount = null, 
                        expiredAt = "2026-12-31T23:59:59"
                    ),
                    VoucherItem(
                        id = 2L,
                        code = "CINE50", 
                        type = "PERCENT", 
                        value = 50.0, 
                        minOrderValue = 300000.0, 
                        maxDiscount = 100000.0, 
                        expiredAt = "2026-06-01T23:59:59"
                    )
                ),
                isLoading = false
            ),
            onNavigate = {},
            onAddClick = {},
            onDeleteVoucher = {},
            onLoadMore = {}
        )
    }
}
