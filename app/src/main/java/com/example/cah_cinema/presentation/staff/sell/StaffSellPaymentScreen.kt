package com.example.cah_cinema.presentation.staff.sell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.ui.theme.CyanBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSellPaymentScreen(
    showtimeId: Long,
    seatIds: List<Long>,
    seatsDisplay: String,
    totalAmount: Double,
    movieTitle: String,
    cinemaName: String,
    startTime: String,
    viewModel: StaffSellTicketViewModel = viewModel(),
    onPaymentSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val paymentState by viewModel.paymentState.collectAsState()

    // Tự động tạo booking khi vào màn hình này
    LaunchedEffect(showtimeId, seatIds) {
        if (paymentState is StaffPaymentState.Idle) {
            viewModel.createBookingAndAwaitPayment(showtimeId)
        }
    }

    LaunchedEffect(paymentState) {
        if (paymentState is StaffPaymentState.Success) {
            onPaymentSuccess()
        }
    }

    Scaffold(
        containerColor = Color(0xFF13131A),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "THANH TOÁN TIỀN MẶT",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = paymentState !is StaffPaymentState.Loading
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1C1C22))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = paymentState) {
                is StaffPaymentState.Idle, is StaffPaymentState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = CyanBlue, strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Đang tạo đơn đặt vé...",
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                is StaffPaymentState.AwaitingPayment -> {
                    PaymentConfirmationContent(
                        bookingId = state.bookingId,
                        totalAmount = state.totalAmount,
                        movieTitle = movieTitle,
                        cinemaName = cinemaName,
                        startTime = startTime,
                        seatsDisplay = seatsDisplay,
                        onConfirmPayment = { viewModel.confirmCashPayment(state.bookingId) }
                    )
                }

                is StaffPaymentState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            state.message,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.resetPayment() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Thử lại", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                is StaffPaymentState.Success -> {
                    // Handled via LaunchedEffect → onPaymentSuccess()
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CyanBlue)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentConfirmationContent(
    bookingId: Long,
    totalAmount: Double,
    movieTitle: String,
    cinemaName: String,
    startTime: String,
    seatsDisplay: String,
    onConfirmPayment: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Booking ID badge
        Surface(
            color = CyanBlue.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = null,
                    tint = CyanBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Mã đặt vé: #$bookingId",
                    color = CyanBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Order summary card
        Surface(
            color = Color(0xFF1C1C22),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Chi tiết đặt vé",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

                PaymentInfoRow(label = "Phim", value = movieTitle)
                PaymentInfoRow(label = "Rạp", value = cinemaName)
                PaymentInfoRow(label = "Suất chiếu", value = formatDisplayTime(startTime))
                PaymentInfoRow(label = "Ghế", value = seatsDisplay)

                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tổng cộng",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        formatVnd(totalAmount),
                        color = CyanBlue,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Payment method indicator
        Surface(
            color = Color(0xFF1C1C22),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Thanh toán tiền mặt",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Nhận tiền từ khách, sau đó xác nhận",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Amount to collect
        Surface(
            color = Color(0xFF4CAF50).copy(alpha = 0.08f),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Số tiền cần thu từ khách",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    formatVnd(totalAmount),
                    color = Color(0xFF4CAF50),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onConfirmPayment,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Xác nhận đã thu tiền",
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Nhấn xác nhận sau khi đã nhận đủ tiền từ khách hàng",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PaymentInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            value,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.6f)
        )
    }
}

// ── Success Screen ─────────────────────────────────────────────────────────────

@Composable
fun StaffPaymentSuccessScreen(
    onSellAnother: () -> Unit,
    onGoHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13131A))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = Color(0xFF4CAF50).copy(alpha = 0.15f),
            shape = CircleShape,
            modifier = Modifier.size(110.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Bán vé thành công!",
            color = Color(0xFF4CAF50),
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Vé đã được tạo và thanh toán thành công",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onSellAnother,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Bán vé tiếp", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onGoHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text("Về Dashboard", fontWeight = FontWeight.Medium, fontSize = 16.sp)
        }
    }
}

private fun formatVnd(amount: Double): String = "%,.0fđ".format(amount)

private fun formatDisplayTime(raw: String): String {
    return try {
        val parts = raw.split("T")
        if (parts.size >= 2) {
            val date = parts[0]
            val time = parts[1].substring(0, 5)
            "$time • $date"
        } else raw
    } catch (e: Exception) {
        raw
    }
}
