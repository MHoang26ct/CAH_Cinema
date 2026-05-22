package com.example.cah_cinema.presentation.user.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cah_cinema.R
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.util.QrCodeGenerator

@Composable
fun TicketDetailScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val ticket = state.recentTicket

    // Generate QR bitmap từ bookingId — chỉ tính lại khi bookingId thay đổi
    val qrBitmap = remember(ticket?.bookingId) {
        ticket?.bookingId?.let { id ->
            if (id > 0) QrCodeGenerator.generate("BOOKING-$id", size = 512) else null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13131A))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBackClick() }
            )
            Text(
                text = "Mã vé",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Thẻ thông tin vé
        ticket?.let { t ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C22))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Poster + tên phim
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        AsyncImage(
                            model = t.posterUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .width(80.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.cinema),
                            placeholder = painterResource(id = R.drawable.cinema)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = t.movieTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                            TicketInfoRow(icon = Icons.Default.LocationOn, text = t.cinemaName)
                            if (t.roomName.isNotEmpty()) {
                                TicketInfoRow(icon = Icons.Default.MeetingRoom, text = "Phòng: ${t.roomName}")
                            }
                            TicketInfoRow(icon = Icons.Default.AccessTime, text = t.showTime)
                        }
                    }

                    if (t.seat.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ghế:",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CyanBlue.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = t.seat,
                                    color = CyanBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (t.totalPrice > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tổng tiền:",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                            Text(
                                text = formatTicketPrice(t.totalPrice),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Hướng dẫn
        Text(
            text = "Xuất trình QR code này tại quầy để nhận vé",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Red,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // QR Code
        Surface(
            modifier = Modifier.size(220.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code vé",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Placeholder khi chưa có bookingId
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ticket_icon),
                            contentDescription = null,
                            tint = Color.Black.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Chưa có mã vé",
                            color = Color.Black.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mã booking
        ticket?.let { t ->
            if (t.bookingId > 0) {
                Text(
                    text = "Mã đặt vé: #${t.bookingId}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun TicketInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CyanBlue,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
            maxLines = 1
        )
    }
}

private fun formatTicketPrice(amount: Double): String {
    val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale("vi", "VN"))
    return "${formatter.format(amount.toLong())} đ"
}
