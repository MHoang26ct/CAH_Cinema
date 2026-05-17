package com.example.cah_cinema.presentation.admin.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.BusinessOverviewResponse
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.admin.components.AdminStatCard
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme

@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    AdminDashboardContent(
        state = state
    )
}

@Composable
fun AdminDashboardContent(
    state: AdminDashboardState,
) {
    AdminScaffold(
        title = "Tổng quan kinh doanh"
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Cyan)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Thống kê hôm nay",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AdminStatCard(
                            title = "Doanh thu",
                            value = "${state.overview?.totalRevenue ?: 0} đ",
                            icon = Icons.Default.AttachMoney,
                            color = Color(0xFF4CAF50)
                        )
                    }
                    item {
                        AdminStatCard(
                            title = "Vé đã bán",
                            value = "${state.overview?.ticketsSold ?: 0}",
                            icon = Icons.Default.ConfirmationNumber,
                            color = Color(0xFF2196F3)
                        )
                    }
                    item {
                        AdminStatCard(
                            title = "Phim đang chiếu",
                            value = "${state.overview?.activeMovies ?: 0}",
                            icon = Icons.Default.Movie,
                            color = Color(0xFFFF9800)
                        )
                    }
                    item {
                        AdminStatCard(
                            title = "Khách hàng mới",
                            value = "24",
                            icon = Icons.Default.Person,
                            color = Color(0xFF9C27B0)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminDashboardPreview() {
    CAH_CinemaTheme {
        AdminDashboardContent(
            state = AdminDashboardState(
                overview = BusinessOverviewResponse(
                    totalRevenue = 25000000.0,
                    ticketRevenue = 20000000.0,
                    foodRevenue = 5000000.0,
                    ticketsSold = 350,
                    activeMovies = 12
                ),
                isLoading = false
            )
        )
    }
}
