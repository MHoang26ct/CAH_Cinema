package com.example.cah_cinema.presentation.user.cinema

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.Showtime
import com.example.cah_cinema.presentation.user.detail.DateSelectionSection
import com.example.cah_cinema.presentation.user.detail.TagBox
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.ui.theme.TextGray

@Composable
fun CinemaDetailScreen(
    viewModel: CinemaDetailViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onShowtimeClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF13131A),
        topBar = {
            CinemaDetailTopBar(
                cinemaName = state.cinemaName,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                if (!state.cinemaImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = state.cinemaImageUrl,
                        contentDescription = state.cinemaName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = state.cinemaName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (state.cinemaAddress.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.cinemaAddress,
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
                
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))

                DateSelectionSection(
                    dates = state.availableDates,
                ) { viewModel.onDateSelected(it) }
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(state.moviesWithShowtimes) { (movie, showtimes) ->
                val selectedDate = state.availableDates.find { it.isSelected }?.date ?: ""
                MovieShowtimeCard(
                    movie = movie,
                    showtimes = showtimes,
                    onShowtimeClick = { showtime ->
                        onShowtimeClick(movie.id, showtime.id, selectedDate, showtime.time)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CinemaDetailTopBar(
    cinemaName: String,
    onBackClick: () -> Unit
) {
    Column {
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
            Text(
                text = cinemaName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieShowtimeCard(
    movie: Movie,
    showtimes: List<Showtime>,
    onShowtimeClick: (Showtime) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2D2D35).copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp, 110.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = movie.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TagBox(text = movie.genre)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        TagBox(text = movie.format)
                        Spacer(modifier = Modifier.width(8.dp))
                        TagBox(text = movie.age, backgroundColor = Color(0xFFFFAA00))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                showtimes.forEach { showtime ->
                    Box(
                        modifier = Modifier
                            .border(1.dp, CyanBlue, RoundedCornerShape(8.dp))
                            .clickable { onShowtimeClick(showtime) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
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
