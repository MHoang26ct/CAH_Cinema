package com.example.cah_cinema.presentation.admin.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.BusinessOverviewResponse
import com.example.cah_cinema.data.model.CinemaRevenueResponse
import com.example.cah_cinema.data.model.DailyRevenueResponse
import com.example.cah_cinema.data.model.MovieRevenueResponse
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.user.booking.formatPrice
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun AdminReportScreen(
    viewModel: AdminReportViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    AdminScaffold(title = "Báo cáo doanh thu") { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyanBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Section: Tổng quan
                state.overview?.let { overview ->
                    item {
                        ReportSectionTitle("Tổng quan (30 ngày gần nhất)")
                        Spacer(modifier = Modifier.height(12.dp))
                        OverviewCards(overview)
                    }
                }

                // Section: Doanh thu theo ngày
                if (state.dailyRevenue.isNotEmpty()) {
                    item {
                        ReportSectionTitle("Doanh thu theo ngày")
                        Spacer(modifier = Modifier.height(12.dp))
                        DailyRevenueTable(state.dailyRevenue)
                    }
                }

                // Section: Doanh thu theo phim
                if (state.movieRevenue.isNotEmpty()) {
                    item {
                        ReportSectionTitle("Doanh thu theo phim")
                        Spacer(modifier = Modifier.height(12.dp))
                        MovieRevenueTable(state.movieRevenue)
                    }
                }

                // Section: Doanh thu theo rạp
                if (state.cinemaRevenue.isNotEmpty()) {
                    item {
                        ReportSectionTitle("Doanh thu theo rạp")
                        Spacer(modifier = Modifier.height(12.dp))
                        CinemaRevenueTable(state.cinemaRevenue)
                    }
                }

                // Empty state
                if (state.overview == null && state.movieRevenue.isEmpty() && state.cinemaRevenue.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.errorMessage ?: "Không có dữ liệu báo cáo",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewCards(overview: BusinessOverviewResponse) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewCard(
                title = "Tổng doanh thu",
                value = formatPrice(overview.totalRevenue),
                icon = Icons.Default.AttachMoney,
                color = CyanBlue,
                modifier = Modifier.weight(1f)
            )
            OverviewCard(
                title = "Doanh thu vé",
                value = formatPrice(overview.ticketRevenue),
                icon = Icons.Default.ConfirmationNumber,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewCard(
                title = "Doanh thu đồ ăn",
                value = formatPrice(overview.foodRevenue),
                icon = Icons.Default.Restaurant,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
            OverviewCard(
                title = "Số vé bán",
                value = "${overview.ticketsSold} vé",
                icon = Icons.Default.Movie,
                color = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun OverviewCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF1C1C22),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ReportSectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun DailyRevenueTable(data: List<DailyRevenueResponse>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C22), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Text("Ngày", color = CyanBlue, modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Số vé", color = CyanBlue, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Doanh thu", color = CyanBlue, modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        // Hiển thị tối đa 10 ngày gần nhất
        data.takeLast(10).forEach { item ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Text(item.date, color = Color.White, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodySmall)
                Text(item.ticketCount.toString(), color = Color.White, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                Text(formatPrice(item.revenue), color = Color.White, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodySmall)
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
        }
    }
}

@Composable
fun MovieRevenueTable(data: List<MovieRevenueResponse>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C22), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Text("Tên phim", color = CyanBlue, modifier = Modifier.weight(3f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Số vé", color = CyanBlue, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Doanh thu", color = CyanBlue, modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        data.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(item.movieTitle, color = Color.White, modifier = Modifier.weight(3f), style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                Text(item.ticketCount.toString(), color = Color.White, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Text(formatPrice(item.revenue), color = Color.White, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
        }
    }
}

@Composable
fun CinemaRevenueTable(data: List<CinemaRevenueResponse>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C22), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Text("Tên rạp", color = CyanBlue, modifier = Modifier.weight(3f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Số vé", color = CyanBlue, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("Doanh thu", color = CyanBlue, modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        data.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(item.cinemaName, color = Color.White, modifier = Modifier.weight(3f), style = MaterialTheme.typography.bodyMedium)
                Text(item.ticketCount.toString(), color = Color.White, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Text(formatPrice(item.revenue), color = Color.White, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
        }
    }
}
