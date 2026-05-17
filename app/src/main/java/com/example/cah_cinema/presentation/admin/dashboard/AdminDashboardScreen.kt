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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.BusinessOverviewResponse
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.admin.components.AdminStatCard
import com.example.cah_cinema.presentation.user.booking.formatPrice

@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    AdminDashboardContent(state = state)
}

@Composable
fun AdminDashboardContent(state: AdminDashboardState) {
    AdminScaffold(title = "Tổng quan kinh doanh") { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00BCD4))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Thống kê 30 ngày gần nhất",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Responsive grid: 2 cột trên phone, 4 cột trên tablet/landscape
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 220.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    // Không dùng fillMaxSize trong scroll — dùng wrapContentHeight
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 400.dp)
                ) {
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
                            value = "${state.overview?.ticketsSold ?: 0} vé",
                            icon = Icons.Default.Movie,
                            color = Color(0xFF9C27B0)
                        )
                    }
                }

                // Thông báo nếu chưa có dữ liệu
                if (state.overview == null && !state.isLoading) {
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
