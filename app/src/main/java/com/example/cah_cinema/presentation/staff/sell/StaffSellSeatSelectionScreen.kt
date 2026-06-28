package com.example.cah_cinema.presentation.staff.sell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.SeatItem
import com.example.cah_cinema.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSellSeatSelectionScreen(
    showtimeId: Long,
    movieTitle: String,
    cinemaName: String,
    roomName: String,
    startTime: String,
    basePrice: Double,
    viewModel: StaffSellTicketViewModel = viewModel(),
    onProceedToPayment: (seatIds: List<Long>, totalAmount: Double) -> Unit,
    onBackClick: () -> Unit
) {
    val seatState by viewModel.seatState.collectAsState()
    val selectedSeats by viewModel.selectedSeats.collectAsState()

    LaunchedEffect(showtimeId) {
        viewModel.loadSeats(showtimeId)
    }

    val total = viewModel.calculateTotal(seatState.seats, basePrice)

    Scaffold(
        containerColor = Color(0xFF13131A),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            movieTitle,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            "$cinemaName • $roomName",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1C1C22))
            )
        },
        bottomBar = {
            if (selectedSeats.isNotEmpty()) {
                Surface(
                    color = Color(0xFF1C1C22),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "${selectedSeats.size} ghế đã chọn",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                            Text(
                                formatVnd(total),
                                color = CyanBlue,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Button(
                            onClick = {
                                onProceedToPayment(
                                    selectedSeats.toList(),
                                    total
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                "Thanh toán",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Showtime info
            Surface(
                color = Color(0xFF1C1C22),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = CyanBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Suất chiếu",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                        Text(
                            formatDisplayTime(startTime),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Giá cơ bản",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                        Text(
                            formatVnd(basePrice),
                            color = CyanBlue,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Màn hình
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(CyanBlue.copy(alpha = 0.5f))
            )
            Text(
                "Màn hình",
                color = CyanBlue.copy(alpha = 0.7f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 4.dp)
            )

            // Seat grid
            when {
                seatState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanBlue)
                    }
                }
                seatState.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            seatState.errorMessage ?: "Lỗi",
                            color = Color.Red
                        )
                    }
                }
                else -> {
                    SeatGrid(
                        seats = seatState.seats,
                        selectedSeats = selectedSeats,
                        onSeatClick = { seatId, isAvailable ->
                            viewModel.toggleSeat(seatId, isAvailable)
                        }
                    )
                }
            }

            // Legend
            SeatLegend()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SeatGrid(
    seats: List<SeatItem>,
    selectedSeats: Set<Long>,
    onSeatClick: (Long, Boolean) -> Unit
) {
    if (seats.isEmpty()) return

    val maxCol = seats.maxOfOrNull { it.col.toInt() } ?: 1
    val maxRow = seats.maxOfOrNull { it.row.toInt() } ?: 1

    // Group by row
    val rowMap = seats.groupBy { it.row.toInt() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        for (row in 1..maxRow) {
            val rowSeats = rowMap[row] ?: continue
            val rowLabel = rowSeats.firstOrNull { it.rowLabel != null }?.rowLabel

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Row label
                Text(
                    rowLabel ?: "",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp,
                    modifier = Modifier.width(20.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(4.dp))

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (col in 1..maxCol) {
                        val seat = rowSeats.find { it.col.toInt() == col }
                        if (seat == null) {
                            // Empty space (aisle)
                            Spacer(modifier = Modifier.size(32.dp))
                        } else {
                            val isSelected = seat.id in selectedSeats
                            val isAvailable = !seat.isSold && !seat.isLocked &&
                                    seat.occupancyStatus == "AVAILABLE"
                            val isAisle = seat.seatType == null || seat.seatType.typeName == "AISLE"

                            if (isAisle) {
                                Spacer(modifier = Modifier.size(32.dp))
                            } else {
                                SeatCell(
                                    seat = seat,
                                    isSelected = isSelected,
                                    isAvailable = isAvailable,
                                    onClick = { onSeatClick(seat.id, isAvailable) }
                                )
                            }
                        }
                        if (col < maxCol) Spacer(modifier = Modifier.width(4.dp))
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    rowLabel ?: "",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp,
                    modifier = Modifier.width(20.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SeatCell(
    seat: SeatItem,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit
) {
    val typeName = seat.seatType?.typeName ?: "REGULAR"
    val isCouple = typeName == "COUPLE"

    val bgColor = when {
        !isAvailable -> NotAvailableGray
        isSelected -> SelectingSeat
        typeName == "VIP" -> VipOrange.copy(alpha = 0.3f)
        typeName == "COUPLE" -> CouplePurple.copy(alpha = 0.3f)
        else -> Color(0xFF2A2A35)
    }
    val borderColor = when {
        isSelected -> SelectingSeat
        !isAvailable -> Color.Transparent
        typeName == "VIP" -> VipOrange
        typeName == "COUPLE" -> CouplePurple
        else -> Color.White.copy(alpha = 0.15f)
    }

    Box(
        modifier = Modifier
            .size(if (isCouple) 66.dp else 30.dp, 30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable(enabled = isAvailable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = seat.colLabel ?: "",
            color = when {
                !isAvailable -> Color.White.copy(alpha = 0.3f)
                isSelected -> Color.Black
                else -> Color.White.copy(alpha = 0.7f)
            },
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SeatLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(color = Color(0xFF2A2A35), border = Color.White.copy(alpha = 0.15f), label = "Thường")
        LegendItem(color = VipOrange.copy(alpha = 0.3f), border = VipOrange, label = "VIP")
        LegendItem(color = CouplePurple.copy(alpha = 0.3f), border = CouplePurple, label = "Đôi")
        LegendItem(color = SelectingSeat, border = SelectingSeat, label = "Đang chọn")
        LegendItem(color = NotAvailableGray, border = Color.Transparent, label = "Đã bán")
    }
}

@Composable
private fun LegendItem(color: Color, border: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
                .border(1.dp, border, RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
    }
}

private fun formatVnd(amount: Double): String {
    return "%,.0fđ".format(amount)
}

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
