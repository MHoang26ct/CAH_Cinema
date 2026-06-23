package com.example.cah_cinema.presentation.admin.components

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cah_cinema.ui.theme.CyanBlue

/**
 * Simplified AdminScaffold - No longer contains the sidebar itself.
 * The Sidebar is now managed globally in MainActivity for persistence.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScaffold(
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF13131A),
        topBar = {
            TopAppBar(
                title = { 
            Text(
                text = title.uppercase(), 
                color = Color.White, 
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF13131A)
                )
            )
        },
        content = content
    )
}

@Composable
fun AdminSidebar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFF1C1C22))
            .padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        // Logo Section
        Row(
            modifier = Modifier.padding(bottom = 40.dp, start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                color = CyanBlue,
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Movie, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "CAH CINEMA",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        val menuItems = listOf(
            SidebarItem("Dashboard", "admin_dashboard", Icons.Default.Dashboard),
            SidebarItem("Quản lý Phim", "admin_movies", Icons.Default.Movie),
            SidebarItem("Quản lý Rạp", "admin_cinemas", Icons.Default.Business),
            SidebarItem("Lịch chiếu", "admin_showtimes", Icons.Default.AccessTime),
            SidebarItem("Voucher", "admin_vouchers", Icons.Default.ConfirmationNumber),
            SidebarItem("Báo cáo", "admin_reports", Icons.Default.BarChart),
            SidebarItem("Cài đặt hệ thống", "admin_settings", Icons.Default.Settings)
        )

        menuItems.forEach { item ->
            SidebarMenuItem(
                item = item,
                isSelected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(bottom = 16.dp))

        SidebarMenuItem(
            item = SidebarItem("Đăng xuất", "logout", Icons.AutoMirrored.Filled.Logout),
            isSelected = false,
            onClick = onLogout,
            activeColor = Color.Red
        )
    }
}

data class SidebarItem(val title: String, val route: String, val icon: ImageVector)

@Composable
fun SidebarMenuItem(
    item: SidebarItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color = CyanBlue
) {
    val horizontalPadding by animateDpAsState(if (isSelected) 16.dp else 12.dp, label = "padding")
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = if (isSelected) activeColor.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = if (isSelected) activeColor else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            
            if (isSelected) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(activeColor, CircleShape)
                )
            }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF1C1C22),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.labelLarge)
                Text(value, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun AdminChartCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF1C1C22),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 28.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SimpleBarChart(
    data: List<Pair<String, Double>>,
    color: Color,
    modifier: Modifier = Modifier
) {
    val maxVal = data.maxOfOrNull { it.second } ?: 1.0
    
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        data.forEach { item ->
            val animatedHeightPercent by animateFloatAsState(
                targetValue = (item.second / maxVal).toFloat().coerceIn(0.05f, 1f),
                animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                label = "barHeight"
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Chart area (Value + Bar) - takes up remaining space
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Value label on top
                    Text(
                        text = if (item.second >= 1_000_000) 
                            "${String.format("%.1f", item.second / 1_000_000)}M" 
                            else "${(item.second / 1000).toInt()}K",
                        color = color,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(animatedHeightPercent)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        color.copy(alpha = 0.9f),
                                        color.copy(alpha = 0.15f)
                                    )
                                ),
                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                    ) {
                        // Accent line at the top
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(color, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .align(Alignment.TopCenter)
                        )
                    }
                }
                
                // Fixed height label area ensures all bars start at same baseline
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = item.first,
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 12.dp, start = 2.dp, end = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SimplePieChart(
    data: List<Pair<String, Double>>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.second }.toFloat()
    if (total == 0f) return

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "pieProgress"
    )

    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Donut Chart
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                data.forEachIndexed { index, pair ->
                    val sweepAngle = (pair.second.toFloat() / total) * 360f * animatedProgress
                    drawArc(
                        color = colors.getOrElse(index) { Color.Gray },
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 30.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                    startAngle += sweepAngle
                }
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TOP 1",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${((data.firstOrNull()?.second ?: 0.0) / total * 100).toInt()}%",
                    color = colors.firstOrNull() ?: Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Legend
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            data.forEachIndexed { index, pair ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(colors.getOrElse(index) { Color.Gray }, CircleShape)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pair.first,
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (pair.second >= 1_000_000) 
                                "${String.format("%.1fM đ", pair.second / 1_000_000)}" 
                                else "${(pair.second / 1000).toInt()}K đ",
                            color = Color.White.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Text(
                        text = "${((pair.second / total) * 100).toInt()}%",
                        color = colors.getOrElse(index) { Color.Gray }.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
