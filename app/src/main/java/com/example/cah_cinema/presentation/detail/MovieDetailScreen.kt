package com.example.cah_cinema.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.domain.model.Cinema
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.MovieDate
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.ui.theme.TextGray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onShowtimeClick: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()

    state.movie?.let { movie ->
        Scaffold(
            containerColor = Color(0xFF13131A)
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    MovieHeaderSection(movie = movie, onBackClick = onBackClick)
                }

                item {
                    MovieDetailsSection(movie = movie)
                }

                if (state.availableDates.isNotEmpty()) {
                    item {
                        DateSelectionSection(
                            dates = state.availableDates,
                            onDateSelected = { viewModel.onDateSelected(it) }
                        )
                    }
                }

                items(state.cinemas) { cinema ->
                    val selectedDate = state.availableDates.find { it.isSelected }?.date ?: ""
                    CinemaSection(
                        cinema = cinema,
                        onShowtimeClick = { showtime ->
                            onShowtimeClick(movie.id, showtime.id, selectedDate, showtime.time)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun MovieHeaderSection(movie: Movie, onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
        // Banner with vertical gradient
        Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
            AsyncImage(
                model = movie.bannerUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF13131A).copy(alpha = 0.5f),
                                Color(0xFF13131A)
                            )
                        )
                    )
            )
        }

        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Floating Info Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF2D2D35).copy(alpha = 0.95f),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp, 140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Thời lượng: ${movie.duration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                    Text(
                        text = movie.genre,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        TagBox(text = movie.format)
                        Spacer(modifier = Modifier.width(8.dp))
                        TagBox(text = movie.age, backgroundColor = Color(0xFFFFAA00))
                    }
                }
            }
        }
    }
}

@Composable
fun TagBox(text: String, backgroundColor: Color = Color.Transparent) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MovieDetailsSection(movie: Movie) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
        DetailRow(label = "Đạo diễn", value = movie.director)
        DetailRow(label = "Diễn viên", value = movie.cast)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Mô tả phim:",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = movie.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
            modifier = Modifier.padding(top = 8.dp),
            lineHeight = 22.sp
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DateSelectionSection(dates: List<MovieDate>, onDateSelected: (MovieDate) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Chọn ngày chiếu:",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dates) { date ->
                Column(
                    modifier = Modifier
                        .width(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (date.isSelected) CyanBlue else Color(0xFF2D2D35))
                        .clickable { onDateSelected(date) }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.dayOfWeek,
                        color = if (date.isSelected) Color.Black else TextGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = date.date,
                        color = if (date.isSelected) Color.Black else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CinemaSection(
    cinema: Cinema,
    onShowtimeClick: (com.example.cah_cinema.domain.model.Showtime) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF2D2D35).copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = cinema.name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = cinema.address,
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                cinema.showtimes.forEach { showtime ->
                    Box(
                        modifier = Modifier
                            .border(1.dp, CyanBlue, RoundedCornerShape(8.dp))
                            .clickable { onShowtimeClick(showtime) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = showtime.time,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieDetailScreenPreview() {
    CAH_CinemaTheme {
        MovieDetailScreen()
    }
}
