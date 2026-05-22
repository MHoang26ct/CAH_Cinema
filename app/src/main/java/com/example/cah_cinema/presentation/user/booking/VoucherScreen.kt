package com.example.cah_cinema.presentation.user.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.VoucherItem
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.presentation.user.booking.formatPrice
import com.example.cah_cinema.presentation.user.booking.VoucherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreen(
    currentTotal: Double,
    viewModel: VoucherViewModel = viewModel(),
    onBackClick: () -> Unit,
    // code, voucherId, discountAmount
    onConfirm: (String, Long, Double) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedVoucher by remember { mutableStateOf<VoucherItem?>(null) }

    Scaffold(
        containerColor = Color(0xFF13131A),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CHỌN VOUCHER", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF13131A))
            )
        },
        bottomBar = {
            selectedVoucher?.let { voucher ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF21212B))
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    val discount = if (voucher.type == "PERCENT") {
                        (currentTotal * voucher.value / 100).coerceAtMost(voucher.maxDiscount ?: Double.MAX_VALUE)
                    } else {
                        voucher.value
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Số tiền được giảm:", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        Text(formatPrice(discount), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tổng cộng sau giảm:", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        Text(formatPrice((currentTotal - discount).coerceAtLeast(0.0)), color = CyanBlue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onConfirm(voucher.code, voucher.id, discount) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("XÁC NHẬN", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyanBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.vouchers) { voucher ->
                    val minVal = voucher.minOrderValue ?: 0.0
                    val isEligible = currentTotal >= minVal
                    VoucherRowItem(
                        voucher = voucher,
                        isSelected = (selectedVoucher?.id == voucher.id),
                        isEligible = isEligible,
                        onSelect = { if (isEligible) selectedVoucher = voucher }
                    )
                }
            }
        }
    }
}

@Composable
fun VoucherRowItem(
    voucher: VoucherItem,
    isSelected: Boolean,
    isEligible: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEligible) { onSelect() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF2C2C3A) else Color(0xFF21212B),
        border = if (isSelected) BorderStroke(1.dp, CyanBlue) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = voucher.code,
                    color = if (isEligible) Color.White else Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = if (voucher.type == "PERCENT") "Giảm ${voucher.value}%" else "Giảm ${formatPrice(voucher.value)}",
                    color = if (isEligible) Color.White.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.3f),
                    fontSize = 12.sp
                )
                if (!isEligible) {
                    val minVal = voucher.minOrderValue ?: 0.0
                    Text(
                        text = "Chưa đủ điều kiện (Tối thiểu ${formatPrice(minVal)})",
                        color = Color.Red.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                enabled = isEligible,
                colors = RadioButtonDefaults.colors(
                    selectedColor = CyanBlue,
                    unselectedColor = Color.White.copy(alpha = 0.5f)
                )
            )
        }
    }
}
