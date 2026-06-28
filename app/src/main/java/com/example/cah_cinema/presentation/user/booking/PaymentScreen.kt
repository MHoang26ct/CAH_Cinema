package com.example.cah_cinema.presentation.user.booking

import com.example.cah_cinema.presentation.user.booking.formatPrice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cah_cinema.R
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    onBackClick: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {},
    onSelectVoucher: (Double) -> Unit = {},
    voucherName: String? = null,
    voucherId: Long? = null,
    voucherDiscount: Double? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Áp dụng voucher khi nhận từ màn hình chọn voucher
    LaunchedEffect(voucherName, voucherId, voucherDiscount) {
        if (voucherName != null && voucherId != null && voucherDiscount != null) {
            viewModel.applyVoucher(voucherName, voucherId, voucherDiscount)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(uiState.isPaymentSuccessful) {
        if (uiState.isPaymentSuccessful) onPaymentSuccess()
    }

    // Tự động mở URL thanh toán khi có payUrl (VNPay/MoMo)
    LaunchedEffect(uiState.paymentUrl) {
        val url = uiState.paymentUrl
        if (!url.isNullOrBlank() && uiState.isWaitingForPayment) {
            try {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                context.startActivity(intent)
            } catch (_: Exception) { /* fallback: user bấm nút thủ công */ }
        }
    }

    // Dialog hết giờ
    if (uiState.isTimeout) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Hết thời gian", fontWeight = FontWeight.Bold) },
            text = { Text("Quá thời gian đặt vé, vui lòng đặt lại.") },
            confirmButton = {
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue)
                ) { Text("ĐẶT LẠI", color = Color.Black) }
            },
            containerColor = Color(0xFF21212B),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f)
        )
    }

    Scaffold(
        containerColor = Color(0xFF13131A),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PaymentTopBar(
                cinemaInfo = uiState.cinemaName.ifEmpty { "Thông tin suất chiếu" },
                dateTimeInfo = "${uiState.date} | ${uiState.showtime}",
                timeLeft = uiState.timeLeftFormatted,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            if (uiState.isWaitingForPayment) {
                PaymentWaitingBottomBar(
                    onCancel = { /* Logic hủy polling hoặc quay lại */ },
                    onOpenApp = {
                        uiState.paymentUrl?.let {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(it))
                            context.startActivity(intent)
                        }
                    }
                )
            } else {
                PaymentBookingBottomBar(
                    totalTickets = uiState.ticketQuantity,
                    totalAmount = uiState.finalAmount,
                    onBookClick = { viewModel.onPaymentClick() },
                    buttonText = if (uiState.isLoading) "ĐANG XỬ LÝ..." else "THANH TOÁN",
                    enabled = !uiState.isLoading
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.isWaitingForPayment) {
                    EPaymentStatusCard(
                        method = uiState.selectedPaymentMethod,
                        qrCodeUrl = uiState.qrCodeUrl,
                        paymentUrl = uiState.paymentUrl
                    )
                } else {
                    // Phần chọn phương thức thanh toán
                    PaymentMethodSelector(
                        selectedMethod = uiState.selectedPaymentMethod,
                        onMethodSelected = { viewModel.onPaymentMethodSelected(it) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Thẻ chi tiết thanh toán
                PaymentDetailsCard(
                    uiState = uiState,
                    onSelectVoucher = { if (!uiState.isWaitingForPayment) onSelectVoucher(uiState.totalAmount) }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PaymentMethodSelector(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit
) {
    Column {
        Text(
            text = "PHƯƠNG THỨC THANH TOÁN",
            color = CyanBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PaymentMethod.entries.forEach { method ->
                val isSelected = method == selectedMethod
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onMethodSelected(method) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) CyanBlue.copy(alpha = 0.15f) else Color(0xFF21212B),
                    border = BorderStroke(1.dp, if (isSelected) CyanBlue else Color.White.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = method.displayName,
                        color = if (isSelected) CyanBlue else Color.White.copy(alpha = 0.7f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentTopBar(
    cinemaInfo: String,
    dateTimeInfo: String,
    timeLeft: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.clickable { onBackClick() }
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cinemaInfo,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = dateTimeInfo,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
        Text(
            text = timeLeft,
            color = Color(0xFFE53935),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PaymentDetailsCard(
    uiState: PaymentUiState,
    onSelectVoucher: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF21212B)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // THÔNG TIN VÉ
            SectionTitle("THÔNG TIN VÉ")
            if (uiState.selectedSeats.isEmpty()) {
                Text(
                    text = "Chưa chọn ghế",
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.selectedSeats.forEach { seatCode ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.White),
                            color = Color.Transparent
                        ) {
                            Text(
                                text = seatCode,
                                color = Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // THÔNG TIN BẮP NƯỚC
            SectionTitle("THÔNG TIN BẮP NƯỚC")
            if (uiState.concessionSummary.isEmpty()) {
                Text(
                    text = "Không có đồ ăn",
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                uiState.concessionSummary.filter { it.quantity > 0 }.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Fit,
                            error = painterResource(id = R.drawable.popcorn)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "x${item.quantity}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
            InfoRow("Tổng bắp nước", formatPrice(uiState.concessionTotal))

            Spacer(modifier = Modifier.height(8.dp))

            // VOUCHER
            SectionTitle("VOUCHER")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectVoucher() }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ticket_icon),
                    contentDescription = null,
                    tint = CyanBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = uiState.selectedVoucherName ?: "Chọn voucher",
                    color = if (uiState.selectedVoucherName != null) CyanBlue else Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f)
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(16.dp))

            // TỔNG KẾT
            SectionTitle("THANH TOÁN")
            // Tiền vé (totalAmount trừ concessionTotal)
            val ticketSubtotal = uiState.totalAmount - uiState.concessionTotal
            if (ticketSubtotal > 0) {
                InfoRow("Tiền vé (${uiState.ticketQuantity} ghế)", formatPrice(ticketSubtotal))
            }
            if (uiState.concessionTotal > 0) {
                InfoRow("Tiền bắp nước", formatPrice(uiState.concessionTotal))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
            InfoRow("Tổng cộng", formatPrice(uiState.totalAmount))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
            InfoRow("Khuyến mãi", "- ${formatPrice(uiState.discount)}")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
            InfoRow("Còn lại", formatPrice(uiState.finalAmount), isHighlight = true)
        }
    }
}

@Composable
fun EPaymentStatusCard(
    method: PaymentMethod,
    qrCodeUrl: String?,
    paymentUrl: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2D2D35).copy(alpha = 0.5f),
        border = BorderStroke(1.dp, CyanBlue.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ĐANG CHỜ THANH TOÁN ${method.displayName}",
                color = CyanBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (!qrCodeUrl.isNullOrBlank()) {
                AsyncImage(
                    model = qrCodeUrl,
                    contentDescription = "QR Code",
                    modifier = Modifier.size(200.dp).background(Color.White).padding(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Quét mã QR để thanh toán",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = CyanBlue,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Vui lòng mở ứng dụng để thanh toán",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = CyanBlue,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
            Text(
                text = "Vui lòng không đóng màn hình này...",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun PaymentWaitingBottomBar(
    onCancel: () -> Unit,
    onOpenApp: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1C1C22),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(50.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("HỦY", color = Color.White)
            }
            Button(
                onClick = onOpenApp,
                modifier = Modifier.weight(1.5f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("MỞ ỨNG DỤNG", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = CyanBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
    }
}

@Composable
fun InfoRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        Text(
            text = value,
            color = if (isHighlight) CyanBlue else Color.White,
            fontSize = if (isHighlight) 16.sp else 14.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun PaymentBookingBottomBar(
    totalTickets: Int,
    totalAmount: Double,
    onBookClick: () -> Unit,
    buttonText: String = "TIẾP TỤC",
    enabled: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1C1C22),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$totalTickets vé",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                Text(
                    text = formatPrice(totalAmount),
                    color = CyanBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onBookClick,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanBlue,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp).padding(start = 16.dp)
            ) {
                Text(
                    text = buttonText,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
