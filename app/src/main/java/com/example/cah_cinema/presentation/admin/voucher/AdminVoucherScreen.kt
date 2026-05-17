package com.example.cah_cinema.presentation.admin.voucher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.cah_cinema.data.model.VoucherItem
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.user.booking.formatPrice
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun AdminVoucherScreen(
    viewModel: AdminVoucherViewModel = viewModel(),
    onNavigate: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    AdminVoucherContent(
        state = state,
        onNavigate = onNavigate,
        onAddClick = { showAddDialog = true }
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
}

@Composable
fun AdminVoucherContent(
    state: AdminVoucherState,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.vouchers) { voucher ->
                        VoucherCard(voucher)
                    }
                }
            }
        }
    }
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo voucher mới", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = code, 
                    onValueChange = { code = it }, 
                    label = { Text("Mã voucher") },
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
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == "FIXED_AMOUNT", onClick = { type = "FIXED_AMOUNT" })
                    Text("Cố định", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = type == "PERCENT", onClick = { type = "PERCENT" })
                    Text("Phần trăm", color = Color.White, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = value, 
                    onValueChange = { value = it }, 
                    label = { Text("Giá trị") },
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
                OutlinedTextField(
                    value = quantity, 
                    onValueChange = { quantity = it }, 
                    label = { Text("Số lượng") },
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
                OutlinedTextField(
                    value = minOrderValue, 
                    onValueChange = { minOrderValue = it }, 
                    label = { Text("Đơn hàng tối thiểu") },
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        CreateVoucherRequest(
                            code = code,
                            type = type,
                            value = value.toDoubleOrNull() ?: 0.0,
                            quantity = quantity.toIntOrNull() ?: 100,
                            startAt = "2026-05-16T00:00:00",
                            expiredAt = "2026-12-31T23:59:59",
                            minOrderValue = minOrderValue.toDoubleOrNull(),
                            maxDiscount = null
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
fun VoucherCard(voucher: VoucherItem) {
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
                Text(text = "Đơn tối thiểu: ${formatPrice(voucher.minOrderValue)}", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
                Text(text = "Hết hạn: ${voucher.expiredAt}", color = Color.White.copy(alpha = 0.3f), style = MaterialTheme.typography.labelSmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { /* Edit */ }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.4f))
                }
                IconButton(onClick = { /* Delete */ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminVoucherPreview() {
    CAH_CinemaTheme {
        AdminVoucherContent(
            state = AdminVoucherState(
                vouchers = listOf(
                    VoucherItem(1, "SUMMER20", "FIXED_AMOUNT", 20000.0, 100000.0, null, "31/12/2026"),
                    VoucherItem(2, "CINE50", "PERCENT", 50.0, 300000.0, 100000.0, "01/06/2026")
                ),
                isLoading = false
            ),
            onNavigate = {},
            onAddClick = {}
        )
    }
}
