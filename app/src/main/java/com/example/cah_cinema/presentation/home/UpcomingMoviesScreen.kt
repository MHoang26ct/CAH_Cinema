package com.example.cah_cinema.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.presentation.component.MoviePosterItem
import com.example.cah_cinema.ui.theme.CyanBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingMoviesScreen(
    viewModel: HomeViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onMovieClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF13131A),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "PHIM SẮP CHIẾU",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF13131A)
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(state.upcomingMovies) { movie ->
                MoviePosterItem(
                    movie = movie,
                    onMovieClick = onMovieClick
                )
            }
        }
    }
}
