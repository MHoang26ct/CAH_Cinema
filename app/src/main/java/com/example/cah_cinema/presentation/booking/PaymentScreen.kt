package com.example.cah_cinema.presentation.booking

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    onPaymentSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle payment success navigation
    androidx.compose.runtime.LaunchedEffect(uiState.isPaymentSuccessful) {
        if (uiState.isPaymentSuccessful) {
            onPaymentSuccess()
        }
    }

    // Timeout Dialog
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
        topBar = {
            PaymentTopBar(
                cinemaInfo = "${uiState.cinemaName} - Phòng: ${uiState.room}",
                dateTimeInfo = "${uiState.date}-${uiState.showtime} | Phòng: ${uiState.subRoom}",
                timeLeft = uiState.timeLeftFormatted,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = { 
                        viewModel.onPaymentClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "THANH TOÁN",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
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
                
                // Movie Info Section
                PaymentMovieInfo(
                    title = uiState.movieTitle,
                    age = uiState.movieAge,
                    posterUrl = uiState.posterUrl,
                    tags = uiState.tags,
                    ageNote = uiState.ageNote,
                    duration = uiState.duration,
                    seat = uiState.selectedSeats.joinToString(" : ") // Separator changed to " : "
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Main Payment Details Card
                PaymentDetailsCard(uiState = uiState)
                
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
    uiState: PaymentUiState
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF21212B)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // TICKET INFO SECTION
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
            
            // CONCESSION INFO SECTION
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // PAYMENT SUMMARY SECTION
            SectionTitle("THANH TOÁN")
            
            InfoRow("Tổng cộng", formatPrice(uiState.totalAmount))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
            InfoRow("Khuyến mãi", formatPrice(uiState.discount))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
            InfoRow("Còn lại", formatPrice(uiState.finalAmount), isHighlight = true)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // PAYMENT VIA QR TRANSFER
            BankingTransferSection(amount = uiState.finalAmount)
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

@Composable
fun BankingTransferSection(
    amount: Double,
    bookingId: String = "36363636"
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Phương thức thanh toán:",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Quét mã QR để chuyển khoản",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Surface(
            modifier = Modifier
                .size(180.dp),
            color = Color.White,
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // QR Placeholder
                Icon(
                    painter = painterResource(id = R.drawable.ticket_icon),
                    contentDescription = "QR Code",
                    tint = Color.Black,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            BankingDetailRow("Số tài khoản:", "1234 5678 9999")
            BankingDetailRow("Tên tài khoản:", "CÔNG TY CAH CINEMA")
            BankingDetailRow("Nội dung:", "CAH $bookingId")
            BankingDetailRow("Số tiền:", formatPrice(amount))
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "* Vui lòng kiểm tra kỹ thông tin trước khi chuyển khoản",
            color = Color.Red.copy(alpha = 0.8f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BankingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
