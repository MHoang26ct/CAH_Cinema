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
import androidx.compose.ui.text.style.TextOverflow
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
    onSeeAllPromotionsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        HomeContent(
            state = state,
            onMovieClick = onMovieClick,
            onPromotionClick = onPromotionClick,
            onSeeAllUpcomingClick = onSeeAllUpcomingClick,
            onSeeAllPromotionsClick = onSeeAllPromotionsClick,
            onProfileClick = onProfileClick,
            onNotificationClick = onNotificationClick
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
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color(0xFF13131A),
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            item {
                HomeHeader(
                    userName = state.userName,
                    avatarUrl = state.avatarUrl,
                    onProfileClick = onProfileClick,
                    onNotificationClick = onNotificationClick
                )
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
fun HomeHeader(
    userName: String,
    avatarUrl: String? = null,
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Phần chào hỏi bên trái
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Chào mừng trở lại 👋",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
            Text(
                text = if (userName.isNotBlank()) userName else "Khách",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Icon thông báo
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFF2D2D35))
                .clickable { onNotificationClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Thông báo",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Avatar — nhấn vào vào Profile
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFF2D2D35))
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
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
