package com.example.cah_cinema.presentation.admin.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.BusinessOverviewResponse
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.admin.components.AdminStatCard
import com.example.cah_cinema.presentation.user.booking.formatPrice
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme

@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    AdminDashboardContent(state = state)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminDashboardPreview() {
    CAH_CinemaTheme {
        AdminDashboardContent(
            state = AdminDashboardState(
                overview = BusinessOverviewResponse(
                    totalRevenue = 125_000_000.0,
                    ticketRevenue = 95_000_000.0,
                    foodRevenue = 30_000_000.0,
                    totalTicketsSold = 2100,
                    totalBookingsPaid = 1850,
                    activeMovies = 12
                ),
                isLoading = false
            )
        )
    }
}

@Composable
fun AdminDashboardContent(state: AdminDashboardState) {
    AdminScaffold(title = "Tổng quan kinh doanh") { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00BCD4))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 220.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Thống kê 30 ngày gần nhất",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    AdminStatCard(
                        title = "Tổng doanh thu",
                        value = formatPrice(state.overview?.totalRevenue ?: 0.0),
                        icon = Icons.Default.AttachMoney,
                        color = Color(0xFF4CAF50)
                    )
                }
                item {
                    AdminStatCard(
                        title = "Doanh thu vé",
                        value = formatPrice(state.overview?.ticketRevenue ?: 0.0),
                        icon = Icons.Default.ConfirmationNumber,
                        color = Color(0xFF2196F3)
                    )
                }
                item {
                    AdminStatCard(
                        title = "Doanh thu đồ ăn",
                        value = formatPrice(state.overview?.foodRevenue ?: 0.0),
                        icon = Icons.Default.Restaurant,
                        color = Color(0xFFFF9800)
                    )
                }
                item {
                    AdminStatCard(
                        title = "Vé đã bán",
                        value = "${state.overview?.totalTicketsSold ?: 0} vé",
                        icon = Icons.Default.Movie,
                        color = Color(0xFF9C27B0)
                    )
                }

                // Thông báo nếu chưa có dữ liệu
                if (state.overview == null && !state.isLoading) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = state.errorMessage ?: "Không có dữ liệu thống kê",
                            color = Color.White.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
