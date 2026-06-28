package com.example.cah_cinema.presentation.staff.sell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.ui.theme.CyanBlue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSellTicketScreen(
    viewModel: StaffSellTicketViewModel = viewModel(),
    onNavigateToSeatSelection: (
        showtimeId: Long,
        movieTitle: String,
        moviePosterUrl: String,
        cinemaName: String,
        roomName: String,
        startTime: String,
        basePrice: Double
    ) -> Unit,
    onBackClick: () -> Unit
) {
    val cinemas by viewModel.cinemas.collectAsState()
    val selectedCinemaId by viewModel.selectedCinemaId.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val showtimes by viewModel.showtimes.collectAsState()
    val isLoading by viewModel.showtimesLoading.collectAsState()
    val error by viewModel.showtimesError.collectAsState()

    Scaffold(
        containerColor = Color(0xFF13131A),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "BÁN VÉ TẠI QUẦY",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Chọn rạp ──────────────────────────────────────────────────────
            if (cinemas.isNotEmpty()) {
                Text(
                    "Chọn rạp",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cinemas) { cinema ->
                        val isSelected = cinema.id == selectedCinemaId
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCinema(cinema.id) },
                            label = { Text(cinema.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanBlue,
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF1C1C22),
                                labelColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }

            // ── Chọn ngày ─────────────────────────────────────────────────────
            Text(
                "Chọn ngày",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
            )
            DateSelector(
                selectedDate = selectedDate,
                onDateSelected = { viewModel.selectDate(it) }
            )

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.06f),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // ── Danh sách suất chiếu ──────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = CyanBlue)
                        }
                    }
                    error != null -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(error ?: "", color = Color.Red)
                        }
                    }
                    showtimes.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.EventBusy,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Không có suất chiếu nào",
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(showtimes) { cinemaShowtime ->
                                MovieShowtimeCard(
                                    item = cinemaShowtime,
                                    onShowtimeClick = { showtime ->
                                        val cinemaName = cinemas
                                            .find { it.id == selectedCinemaId }?.name ?: ""
                                        onNavigateToSeatSelection(
                                            showtime.id,
                                            cinemaShowtime.movie.title,
                                            cinemaShowtime.movie.posterUrl ?: "",
                                            cinemaName,
                                            showtime.roomName,
                                            showtime.startTime,
                                            showtime.basePrice
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateSelector(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val today = LocalDate.now()
    val dates = (0..6).map { today.plusDays(it.toLong()) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dates) { date ->
            val dateStr = date.format(formatter)
            val isSelected = dateStr == selectedDate
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("vi"))
            val dayOfMonth = date.dayOfMonth

            Surface(
                modifier = Modifier
                    .width(56.dp)
                    .clickable { onDateSelected(dateStr) },
                color = if (isSelected) CyanBlue else Color(0xFF1C1C22),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        dayOfWeek.uppercase(),
                        color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        dayOfMonth.toString(),
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieShowtimeCard(
    item: CinemaShowtimeItem,
    onShowtimeClick: (ShowtimeInfo) -> Unit
) {
    Surface(
        color = Color(0xFF1C1C22),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Poster
            AsyncImage(
                model = item.movie.posterUrl,
                contentDescription = item.movie.title,
                modifier = Modifier
                    .width(70.dp)
                    .height(100.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.movie.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    if (item.movie.ageRating != null) {
                        Surface(
                            color = Color(0xFFFFAA00).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                item.movie.ageRating,
                                color = Color(0xFFFFAA00),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Showtimes buttons
                item.showtimes.forEach { showtime ->
                    if (showtime.status != "HIDDEN" && showtime.status != "CANCELLED") {
                        ShowtimeButton(showtime = showtime, onClick = { onShowtimeClick(showtime) })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowtimeButton(
    showtime: ShowtimeInfo,
    onClick: () -> Unit
) {
    val isSoldOut = showtime.status == "SOLD_OUT"
    val timeDisplay = try {
        val parts = showtime.startTime.split("T")
        if (parts.size >= 2) parts[1].substring(0, 5) else showtime.startTime
    } catch (e: Exception) {
        showtime.startTime
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isSoldOut) { onClick() },
        color = if (isSoldOut) Color.White.copy(alpha = 0.05f) else CyanBlue.copy(alpha = 0.1f),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSoldOut) Color.White.copy(alpha = 0.1f) else CyanBlue.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = if (isSoldOut) Color.White.copy(alpha = 0.3f) else CyanBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        timeDisplay,
                        color = if (isSoldOut) Color.White.copy(alpha = 0.3f) else Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${showtime.format} • ${showtime.roomName}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSoldOut) {
                    Text("Hết vé", color = Color.Red.copy(alpha = 0.6f), fontSize = 12.sp)
                } else {
                    Text(
                        formatVnd(showtime.basePrice),
                        color = CyanBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = CyanBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private fun formatVnd(amount: Double): String {
    val formatted = "%,.0f".format(amount)
    return "${formatted}đ"
}
