package com.example.cah_cinema.presentation.admin.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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

@Composable
fun AdminReportContent(
    state: AdminReportState
) {
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
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Section: Doanh thu theo phim
                item {
                    ReportSectionTitle("Doanh thu theo phim")
                    Spacer(modifier = Modifier.height(16.dp))
                    MovieRevenueTable(state.movieRevenue)
                }

                // Section: Doanh thu theo rạp
                item {
                    ReportSectionTitle("Doanh thu theo rạp")
                    Spacer(modifier = Modifier.height(16.dp))
                    CinemaRevenueTable(state.cinemaRevenue)
                }

                // Section: Tổng quan (nếu có)
                state.businessOverview?.let { overview ->
                    item {
                        ReportSectionTitle("Tổng quan kinh doanh")
                        Spacer(modifier = Modifier.height(16.dp))
                        BusinessOverviewCard(overview)
                    }
                }
            }
        }
    }
}

@Composable
fun BusinessOverviewCard(overview: com.example.cah_cinema.data.model.BusinessOverviewResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C22), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OverviewRow("Tổng doanh thu", formatPrice(overview.totalRevenue))
        OverviewRow("Doanh thu vé", formatPrice(overview.ticketRevenue))
        OverviewRow("Doanh thu đồ ăn", formatPrice(overview.foodRevenue))
        OverviewRow("Tổng số vé bán", overview.totalTicketsSold.toString())
        OverviewRow("Tổng số đơn hàng", overview.totalBookingsPaid.toString())
        OverviewRow("Tổng giảm giá", formatPrice(overview.totalDiscount))
        OverviewRow("Giá trị trung bình đơn", formatPrice(overview.averageOrderValue))
    }
}

@Composable
fun OverviewRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
        Text(value, color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
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
                Text(item.movieTitle, color = Color.White, modifier = Modifier.weight(3f), style = MaterialTheme.typography.bodyMedium)
                Text(item.ticketsSold.toString(), color = Color.White, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Text(formatPrice(item.ticketRevenue), color = Color.White, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodyMedium)
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
                Text(item.ticketsSold.toString(), color = Color.White, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Text(formatPrice(item.ticketRevenue), color = Color.White, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminReportPreview() {
    CAH_CinemaTheme {
        AdminReportContent(
            state = AdminReportState(
                movieRevenue = listOf(
                    MovieRevenueResponse(1, "HẸN EM NGÀY NHẬT THỰC", 15000000.0, 200),
                    MovieRevenueResponse(2, "KUNG FU PANDA 4", 8000000.0, 120)
                ),
                cinemaRevenue = listOf(
                    CinemaRevenueResponse(1, "Cinestar Quốc Thanh", 12000000.0, 150),
                    CinemaRevenueResponse(2, "Cinestar Hai Bà Trưng", 11000000.0, 170)
                )
            )
        )
    }
}
