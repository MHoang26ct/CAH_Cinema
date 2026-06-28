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
import com.example.cah_cinema.data.model.CinemaRevenueResponse
import com.example.cah_cinema.data.model.MovieRevenueResponse
import com.example.cah_cinema.presentation.admin.components.AdminChartCard
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.admin.components.AdminStatCard
import com.example.cah_cinema.presentation.admin.components.SimpleBarChart
import com.example.cah_cinema.presentation.admin.components.SimplePieChart
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme

data class AdminDashboardState(
    val overview: BusinessOverviewResponse? = null,
    val movieRevenue: List<MovieRevenueResponse> = emptyList(),
    val cinemaRevenue: List<CinemaRevenueResponse> = emptyList(),
    val isLoading: Boolean = false
)

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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    item {
                        AdminStatCard(
                            title = "Doanh thu",
                            value = "${state.overview?.totalRevenue?.toInt() ?: 0} đ",
                            icon = Icons.Default.AttachMoney,
                            color = Color(0xFF4CAF50)
                        )
                    }
                    item {
                        AdminStatCard(
                            title = "Vé đã bán",
                            value = "${state.overview?.totalTicketsSold ?: 0}",
                            icon = Icons.Default.ConfirmationNumber,
                            color = Color(0xFF2196F3)
                        )
                    }
                    item {
                        AdminStatCard(
                            title = "Đơn thanh toán",
                            value = "${state.overview?.totalBookingsPaid ?: 0}",
                            icon = Icons.Default.Person,
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

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    val pieChartColors = listOf(
                        Color(0xFF00BCD4),
                        Color(0xFF4CAF50),
                        Color(0xFFFF9800),
                        Color(0xFFE91E63),
                        Color(0xFF9C27B0)
                    )

                    AdminChartCard(
                        title = "Top Phim Doanh Thu",
                        modifier = Modifier.weight(1.2f)
                    ) {
                        SimplePieChart(
                            data = state.movieRevenue.take(5).map { it.movieTitle to it.ticketRevenue },
                            colors = pieChartColors
                        )
                    }

                    AdminChartCard(
                        title = "Doanh Thu Theo Rạp",
                        modifier = Modifier.weight(1f)
                    ) {
                        SimpleBarChart(
                            data = state.cinemaRevenue.take(5).map { it.cinemaName to it.ticketRevenue },
                            color = Color(0xFFFFC107)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
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
                    from = "2026-05-19",
                    to = "2026-05-19",
                    totalRevenue = 25000000.0,
                    ticketRevenue = 20000000.0,
                    foodRevenue = 5000000.0,
                    totalTicketsSold = 350,
                    totalBookingsPaid = 120,
                    totalDiscount = 500000.0,
                    averageOrderValue = 208333.33
                ),
                movieRevenue = listOf(
                    MovieRevenueResponse(1, "Avengers: Endgame", 5000000.0, 100),
                    MovieRevenueResponse(2, "Avatar 2", 4500000.0, 90),
                    MovieRevenueResponse(3, "Spider-Man", 3000000.0, 60),
                    MovieRevenueResponse(4, "Inception", 2500000.0, 50),
                    MovieRevenueResponse(5, "Batman", 2000000.0, 40)
                ),
                cinemaRevenue = listOf(
                    CinemaRevenueResponse(1, "CGV Vincom", 8000000.0, 150),
                    CinemaRevenueResponse(2, "Lotte Cinema", 7000000.0, 130),
                    CinemaRevenueResponse(3, "BHD Star", 5000000.0, 100),
                    CinemaRevenueResponse(4, "Galaxy Cinema", 4000000.0, 80),
                    CinemaRevenueResponse(5, "Cinestar", 1000000.0, 20)
                ),
                isLoading = false
            )
        )
    }
}
