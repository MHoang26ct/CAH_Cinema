package com.example.cah_cinema.presentation.user.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.domain.model.Concession
import com.example.cah_cinema.domain.model.ConcessionType
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun ConcessionScreen(
    viewModel: ConcessionViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onPaymentClick: (Double) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    
    // Sử dụng trực tiếp totalAmount từ state để UI cập nhật tức thì
    val totalAmount = state.totalAmount

    Scaffold(
        containerColor = Color(0xFF13131A) ,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Nước và bắp",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        bottomBar = {
            BookingBottomBar(
                totalTickets = state.selectedSeatsDisplay.split(" : ").filter { it.isNotEmpty() }.size,
                totalAmount = totalAmount,
                onBookClick = { onPaymentClick(totalAmount) },
                buttonText = "Thanh toán",
                selectedSeatsDisplay = state.selectedSeatsDisplay
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            val drinks = state.concessions.filter { it.type == ConcessionType.DRINK }
            val popcorns = state.concessions.filter { it.type == ConcessionType.POPCORN }

            if (drinks.isNotEmpty()) {
                item {
                    Text(
                        text = "Nước uống",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                items(drinks) { concession ->
                    ConcessionItem(
                        concession = concession,
                    ) { delta -> viewModel.onQuantityChange(concession.id, delta) }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (popcorns.isNotEmpty()) {
                item {
                    Text(
                        text = "Bắp",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                items(popcorns) { concession ->
                    ConcessionItem(
                        concession = concession,
                    ) { delta -> viewModel.onQuantityChange(concession.id, delta) }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ConcessionItem(
    concession: Concession,
    onQuantityChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2D2D35).copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = concession.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = concession.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatPrice(concession.price),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            // Nút tăng/giảm số lượng thiết kế dọc
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = CyanBlue,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onQuantityChange(1) }
                )
                
                Text(
                    text = concession.quantity.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Remove",
                    tint = CyanBlue,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onQuantityChange(-1) }
                )
            }
        }
    }
}
