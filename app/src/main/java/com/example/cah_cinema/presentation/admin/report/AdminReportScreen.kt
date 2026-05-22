package com.example.cah_cinema.presentation.admin.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.BusinessOverviewResponse
import com.example.cah_cinema.data.model.CinemaRevenueResponse
import com.example.cah_cinema.data.model.DailyRevenueResponse
import com.example.cah_cinema.data.model.MovieRevenueResponse
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.user.booking.formatPrice
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun AdminReportScreen(
    viewModel: AdminReportViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    AdminReportContent(state = state)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OverviewCards(overview: BusinessOverviewResponse) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = 4
    ) {
        val cardModifier = Modifier
            .weight(1f)
            .widthIn(min = 200.dp)

        OverviewCard(
            title = "Tổng doanh thu",
            value = formatPrice(overview.totalRevenue),
            icon = Icons.Default.AttachMoney,
            color = CyanBlue,
            modifier = cardModifier
        )
        OverviewCard(
            title = "Doanh thu vé",
            value = formatPrice(overview.ticketRevenue),
            icon = Icons.Default.ConfirmationNumber,
            color = Color(0xFF4CAF50),
            modifier = cardModifier
        )
        OverviewCard(
            title = "Doanh thu đồ ăn",
            value = formatPrice(overview.foodRevenue),
            icon = Icons.Default.Restaurant,
            color = Color(0xFFFF9800),
            modifier = cardModifier
        )
        OverviewCard(
            title = "Số vé bán",
            value = "${overview.totalTicketsSold} vé",
            icon = Icons.Default.Movie,
            color = Color(0xFF9C27B0),
            modifier = cardModifier
        )
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
                Text(title, color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge  // nhất quán với AdminStatCard
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminReportPreview() {
    CAH_CinemaTheme {
        AdminReportContent(
            state = AdminReportState(
                overview = BusinessOverviewResponse(
                    totalRevenue = 125_000_000.0,
                    ticketRevenue = 95_000_000.0,
                    foodRevenue = 30_000_000.0,
                    totalTicketsSold = 2100,
                    totalBookingsPaid = 1850,
                    activeMovies = 12
                ),
                dailyRevenue = listOf(
                    DailyRevenueResponse("2026-05-19", 4_200_000.0, 93),
                    DailyRevenueResponse("2026-05-20", 5_800_000.0, 128),
                    DailyRevenueResponse("2026-05-21", 3_900_000.0, 86)
                ),
                movieRevenue = listOf(
                    MovieRevenueResponse(1, "Avengers: Endgame", 42_000_000.0, 930),
                    MovieRevenueResponse(2, "Spider-Man: No Way Home", 38_500_000.0, 855)
                ),
                cinemaRevenue = listOf(
                    CinemaRevenueResponse(1, "Cinestar Quốc Thanh", 68_000_000.0, 1510),
                    CinemaRevenueResponse(2, "Cinestar Hai Bà Trưng", 57_000_000.0, 590)
                ),
                isLoading = false
            )
        )
    }
}

@Composable
fun AdminReportContent(state: AdminReportState) {
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
                state.overview?.let { overview ->
                    item {
                        ReportSectionTitle("Tổng quan (30 ngày gần nhất)")
                        Spacer(modifier = Modifier.height(12.dp))
                        OverviewCards(overview)
                    }
                }
                if (state.dailyRevenue.isNotEmpty()) {
                    item {
                        ReportSectionTitle("Doanh thu theo ngày")
                        Spacer(modifier = Modifier.height(12.dp))
                        DailyRevenueTable(state.dailyRevenue)
                    }
                }
                if (state.movieRevenue.isNotEmpty()) {
                    item {
                        ReportSectionTitle("Doanh thu theo phim")
                        Spacer(modifier = Modifier.height(12.dp))
                        MovieRevenueTable(state.movieRevenue)
                    }
                }
                if (state.cinemaRevenue.isNotEmpty()) {
                    item {
                        ReportSectionTitle("Doanh thu theo rạp")
                        Spacer(modifier = Modifier.height(12.dp))
                        CinemaRevenueTable(state.cinemaRevenue)
                    }
                }
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
