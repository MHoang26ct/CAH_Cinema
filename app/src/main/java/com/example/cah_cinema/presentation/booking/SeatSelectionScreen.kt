package com.example.cah_cinema.presentation.booking

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.domain.model.Seat
import com.example.cah_cinema.domain.model.SeatStatus
import com.example.cah_cinema.domain.model.SeatType

@Composable
fun SeatSelectionScreen(
    viewModel: SeatSelectionViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onConfirmClick: (String, Double) -> Unit = { _, _ -> },
) {
    val state by viewModel.state.collectAsState()
    val totalAmount = viewModel.getTotalAmount()
    val selectedSeatsDisplay = state.selectedSeats.joinToString(" : ") { it.name }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        containerColor = Color(0xFF13131A),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TicketTopBar(
                title = state.movie?.title ?: "",
                tags = listOf(state.movie?.genre ?: "", state.movie?.format ?: "", state.movie?.age ?: ""),
                showtime = state.selectedShowtime,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BookingBottomBar(
                totalTickets = state.selectedSeats.size,
                totalAmount = totalAmount,
                onBookClick = {
                    onConfirmClick(selectedSeatsDisplay, totalAmount)
                },
                buttonText = "Chọn chỗ",
                selectedSeatsDisplay = selectedSeatsDisplay
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                MovieInfoSection(
                    movieTitle = state.movie?.title ?: "",
                    posterUrl = state.movie?.posterUrl ?: "",
                    age = state.movie?.age ?: "",
                    cinemaName = state.selectedCinemaName,
                    room = state.selectedRoom,
                    showtime = state.selectedShowtime,
                    date = state.selectedDate
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                ScreenSection()
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                SeatGridSection(
                    seats = state.seats,
                    onSeatClick = { viewModel.onSeatClick(it) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                SeatLegendSection()
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ScreenSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 48.dp)) {
            val path = Path().apply {
                moveTo(0f, size.height)
                quadraticTo(size.width / 2, 0f, size.width, size.height)
            }
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.8f),
                style = Stroke(width = 6f)
            )
        }
        Text(
            text = "MÀN HÌNH",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        )
    }
}

@Composable
fun SeatGridSection(
    seats: List<Seat>,
    onSeatClick: (Seat) -> Unit
) {
    val rows = seats.groupBy { it.row }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        rows.forEach { (_, rowSeats) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowSeats.forEach { seat ->
                    SeatItem(seat = seat, onClick = { onSeatClick(seat) })
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}

@Composable
fun SeatItem(seat: Seat, onClick: () -> Unit) {
    val backgroundColor = when (seat.status) {
        SeatStatus.SELECTED -> Color(0xFF00E5FF)
        SeatStatus.TAKEN_BY_OTHERS -> Color(0xFFB71C1C)
        SeatStatus.MAINTENANCE -> Color(0xFF757575)
        SeatStatus.BOOKED -> Color(0xFF26262E)
        SeatStatus.AVAILABLE -> when (seat.type) {
            SeatType.REGULAR -> Color(0xFF2E7D32)
            SeatType.VIP -> Color(0xFFD87D4A)
            SeatType.COUPLE -> Color(0xFFC2185B)
        }
    }

    Box(
        modifier = Modifier
            .size(if (seat.type == SeatType.COUPLE) 44.dp else 19.dp, 19.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (seat.type != SeatType.COUPLE) {
            Text(
                text = seat.name,
                color = if (seat.status == SeatStatus.SELECTED) Color.Black else Color.White,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = seat.number,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SeatLegendSection() {
    val legends = listOf(
        Pair("thường", Color(0xFF2E7D32)),
        Pair("vip", Color(0xFFD87D4A)),
        Pair("cặp đôi", Color(0xFFC2185B)),
        Pair("bảo trì", Color(0xFF757575)),
        Pair("đang chọn", Color(0xFF00E5FF)),
        Pair("được chọn bởi người dùng khác", Color(0xFFB71C1C)),
        Pair("đã được đặt", Color(0xFF26262E))
    )

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        legends.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .size(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(item.second))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = item.first, color = Color.White, fontSize = 11.sp)
            }
        }
    }
}
