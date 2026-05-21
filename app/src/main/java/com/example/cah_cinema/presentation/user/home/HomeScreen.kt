package com.example.cah_cinema.presentation.user.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.presentation.component.FeaturedMovieCard
import com.example.cah_cinema.presentation.component.MoviePosterItem
import com.example.cah_cinema.presentation.component.FullScreenLoading
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.ui.theme.TextGray

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onMovieClick: (String) -> Unit = {},
    onPromotionClick: (String) -> Unit = {},
    onSeeAllUpcomingClick: () -> Unit = {},
    onSeeAllPromotionsClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        HomeContent(
            state = state,
            onMovieClick = onMovieClick,
            onPromotionClick = onPromotionClick,
            onSeeAllUpcomingClick = onSeeAllUpcomingClick,
            onSeeAllPromotionsClick = onSeeAllPromotionsClick
        )
        
        if (state.isLoading) {
            FullScreenLoading()
        }
    }
}

@Composable
fun HomeContent(
    state: HomeState,
    onMovieClick: (String) -> Unit,
    onPromotionClick: (String) -> Unit,
    onSeeAllUpcomingClick: () -> Unit,
    onSeeAllPromotionsClick: () -> Unit,
) {
    Scaffold(
        containerColor = Color(0xFF13131A),
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            item {
                HomeHeader(userName = state.userName)
            }

            // Phim nổi bật
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    items(state.featuredMovies) { movie ->
                        FeaturedMovieCard(
                            movie = movie,
                            onBookTicket = { onMovieClick(movie.id) },
                            onMovieClick = onMovieClick
                        )
                    }
                }
            }

            // Phim sắp chiếu
            item {
                SectionHeader(
                    title = "Sắp chiếu",
                    onSeeAllClick = onSeeAllUpcomingClick
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    items(state.upcomingMovies) { movie ->
                        MoviePosterItem(
                            movie = movie,
                            onMovieClick = onMovieClick
                        )
                    }
                }
            }

            // Chương trình khuyến mãi
            item {
                SectionHeader(
                    title = "Promotion",
                    onSeeAllClick = onSeeAllPromotionsClick
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.promotions) { promotion ->
                        AsyncImage(
                            model = promotion.imageUrl,
                            contentDescription = promotion.title,
                            modifier = Modifier
                                .width(300.dp)
                                .height(160.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onPromotionClick(promotion.id) },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun HomeHeader(userName: String) {
// ... (rest remains same)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Chào mừng",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2D2D35))
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 32.dp, bottom = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Xem tất cả",
            color = CyanBlue,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}
