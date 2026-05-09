package com.example.cah_cinema.presentation.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cah_cinema.ui.theme.CyanBlue
import java.text.NumberFormat
import java.util.*

@Composable
fun TicketTopBar(
    title: String,
    tags: List<String>,
    showtime: String,
    availableShowtimes: List<String> = emptyList(),
    onShowtimeChange: (String) -> Unit = {},
    onBackClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tags.forEach { tag ->
                if (tag.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (tag == "T16" || tag == "T13" || tag.startsWith("T")) Color(0xFFFFAA00) else Color.Transparent,
                        border = if (!tag.startsWith("T")) BorderStroke(1.dp, CyanBlue) else null
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Box {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, CyanBlue),
                    modifier = Modifier.clickable(enabled = availableShowtimes.isNotEmpty()) { expanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = showtime, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        if (availableShowtimes.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown, 
                                contentDescription = null, 
                                tint = Color.White, 
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                if (availableShowtimes.isNotEmpty()) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF2D2D35))
                    ) {
                        availableShowtimes.forEach { time ->
                            DropdownMenuItem(
                                text = { Text(text = time, color = Color.White) },
                                onClick = {
                                    onShowtimeChange(time)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieInfoSection(
    movieTitle: String,
    posterUrl: String,
    age: String,
    cinemaName: String,
    room: String,
    showtime: String,
    date: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = posterUrl,
            contentDescription = null,
            modifier = Modifier
                .width(100.dp)
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = "$movieTitle ($age)",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn, 
                    contentDescription = null, 
                    tint = CyanBlue, 
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$cinemaName - $room",
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule, 
                    contentDescription = null, 
                    tint = CyanBlue, 
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Suất chiếu $showtime - $date",
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun BookingBottomBar(
    totalTickets: Int,
    totalAmount: Double,
    onBookClick: () -> Unit,
    buttonText: String = "Đặt vé",
    selectedSeatsDisplay: String = ""
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1C1C24),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$totalTickets Ghế",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (selectedSeatsDisplay.isNotEmpty()) {
                        Text(
                            text = ": ",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedSeatsDisplay,
                            color = CyanBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Tổng cộng: ${formatPrice(totalAmount)}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Button(
                onClick = onBookClick,
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(54.dp)
                    .width(130.dp),
                enabled = totalTickets > 0
            ) {
                Text(
                    text = buttonText.uppercase(), 
                    color = Color.Black, 
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"))
    return "${formatter.format(price)} đ"
}
