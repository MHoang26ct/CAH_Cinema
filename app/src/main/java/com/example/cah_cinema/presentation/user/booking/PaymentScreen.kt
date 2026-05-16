package com.example.cah_cinema.presentation.user.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.R
import com.example.cah_cinema.ui.theme.CyanBlue

/**
 * PaymentScreen - Frontend implementation matching the design.
 */
@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {},
    onSelectVoucher: (Double) -> Unit = {},
    voucherName: String? = null,
    voucherDiscount: Double? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Cập nhật voucher nếu có từ màn hình chọn voucher
    androidx.compose.runtime.LaunchedEffect(voucherName, voucherDiscount) {
        if (voucherName != null && voucherDiscount != null) {
            viewModel.applyVoucher(voucherName, voucherDiscount)
        }
    }

    // Hiển thị thông báo lỗi nếu có
    androidx.compose.runtime.LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Xử lý điều hướng khi thanh toán thành công
    androidx.compose.runtime.LaunchedEffect(uiState.isPaymentSuccessful) {
        if (uiState.isPaymentSuccessful) {
            onPaymentSuccess()
        }
    }

    // Hộp thoại hết giờ
    if (uiState.isTimeout) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Hết thời gian", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Quá thời gian đặt vé, vui lòng đặt lại.") },
            confirmButton = {
                Button(
                    onClick = { onBackClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue)
                ) {
                    Text("ĐẶT LẠI", color = Color.Black)
                }
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
                cinemaInfo = "${uiState.cinemaName} - Phòng: ${uiState.room}",
                dateTimeInfo = "${uiState.date}-${uiState.showtime} | Phòng: ${uiState.subRoom}",
                timeLeft = uiState.timeLeftFormatted,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BookingBottomBar(
                totalTickets = uiState.ticketQuantity,
                totalAmount = uiState.finalAmount,
                onBookClick = {
                    viewModel.onPaymentClick()
                },
                buttonText = if (uiState.isLoading) "ĐANG XỬ LÝ..." else "THANH TOÁN",
                enabled = !uiState.isLoading
            )
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
                
                // Phần thông tin phim
                PaymentMovieInfo(
                    title = uiState.movieTitle,
                    age = uiState.movieAge,
                    posterUrl = uiState.posterUrl,
                    tags = uiState.tags,
                    ageNote = uiState.ageNote,
                    duration = uiState.duration,
                    seat = uiState.selectedSeats.joinToString(" : ") // Dấu phân cách đã đổi thành " : "
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Thẻ chi tiết thanh toán chính
                PaymentDetailsCard(
                    uiState = uiState,
                    onSelectVoucher = { onSelectVoucher(uiState.totalAmount) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
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
            color = Color(0xFFE53935), // Red color for timer
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PaymentMovieInfo(
    title: String,
    age: String,
    posterUrl: String,
    tags: List<String>,
    ageNote: String,
    duration: String,
    seat: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = posterUrl,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.ic_launcher_background),
            modifier = Modifier
                .width(90.dp)
                .height(130.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "$title ($age)",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tags.forEach { tag ->
                    val isAgeTag = tag == "T16" || tag == "T18" || tag == "P"
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isAgeTag) Color(0xFFFFAA00) else Color.Transparent,
                        border = if (!isAgeTag) BorderStroke(1.dp, CyanBlue) else null
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = ageNote, color = Color(0xFFE53935), fontSize = 12.sp)
            Text(text = "Thời lượng : $duration", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Text(text = seat, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
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
            // PHẦN THÔNG TIN VÉ
            SectionTitle("THÔNG TIN VÉ")
            
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
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = seatCode, color = Color.White, fontSize = 13.sp)
                            Text(
                                text = formatPrice(uiState.ticketPricePerSeat).removeSuffix(" đ"), 
                                color = Color.White, 
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // PHẦN THÔNG TIN BẮP NƯỚC
            SectionTitle("THÔNG TIN BẮP NƯỚC")
            
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pepsi),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = uiState.concessionName, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text(text = uiState.concessionQuantity.toString(), color = Color.White, fontSize = 14.sp)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
            InfoRow("Tổng", formatPrice(uiState.concessionTotal))
            
            Spacer(modifier = Modifier.height(8.dp))

            // PHẦN VOUCHER
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
                    color = Color.White,
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

            Spacer(modifier = Modifier.height(24.dp))
            
            // PHẦN TỔNG KẾT THANH TOÁN
            SectionTitle("THANH TOÁN")
            
            InfoRow("Tổng cộng", formatPrice(uiState.totalAmount))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
            InfoRow("Khuyến mãi", formatPrice(uiState.discount))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
            InfoRow("Còn lại", formatPrice(uiState.finalAmount), isHighlight = true)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
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
            color = Color.White, 
            fontSize = 14.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
        )
    }
}
