package com.example.cah_cinema.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.domain.model.TicketType
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun TicketSelectionScreen(
    viewModel: TicketSelectionViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onBookClick: (Int, Int, Double) -> Unit = { _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    
    val totalAmount by remember {
        derivedStateOf { state.ticketTypes.sumOf { it.price * it.quantity } }
    }
    val totalTickets by remember {
        derivedStateOf { state.ticketTypes.sumOf { it.quantity } }
    }

    Scaffold(
        containerColor = Color(0xFF13131A),
        topBar = {
            TicketTopBar(
                title = state.movie?.title ?: "",
                tags = listOf(state.movie?.genre ?: "", state.movie?.format ?: "", state.movie?.age ?: ""),
                showtime = state.selectedShowtime,
                availableShowtimes = state.availableShowtimes,
                onShowtimeChange = { viewModel.onShowtimeChange(it) },
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BookingBottomBar(
                totalTickets = totalTickets,
                totalAmount = totalAmount,
                onBookClick = {
                    val regularCount = state.ticketTypes.filter { it.id != "4" }.sumOf { it.quantity }
                    val coupleCount = state.ticketTypes.filter { it.id == "4" }.sumOf { it.quantity }
                    onBookClick(regularCount, coupleCount, totalAmount)
                },
                buttonText = "Đặt vé"
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                MovieInfoSection(
                    movieTitle = state.movie?.title ?: "",
                    posterUrl = state.movie?.posterUrl ?: "",
                    age = state.movie?.age ?: "",
                    cinemaName = state.selectedCinemaName,
                    room = state.selectedRoom,
                    showtime = state.selectedShowtime,
                    date = state.selectedDate
                )
            }

            items(state.ticketTypes) { ticketType ->
                TicketTypeItem(
                    ticketType = ticketType,
                    onQuantityChange = { delta ->
                        viewModel.onQuantityChange(ticketType.id, delta)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun TicketTypeItem(
    ticketType: TicketType,
    onQuantityChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2D2D35).copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = ticketType.name,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = formatPrice(ticketType.price),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFF13131A), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(-1) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove, 
                        contentDescription = null, 
                        tint = CyanBlue, 
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Text(
                    text = ticketType.quantity.toString(),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { onQuantityChange(1) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, 
                        contentDescription = null, 
                        tint = CyanBlue, 
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
