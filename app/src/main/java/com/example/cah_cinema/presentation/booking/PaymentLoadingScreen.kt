package com.example.cah_cinema.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cah_cinema.ui.theme.CyanBlue
import kotlinx.coroutines.delay

@Composable
fun PaymentLoadingScreen(
    onLoadingComplete: () -> Unit
) {
    LaunchedEffect(Unit) {
        // Simulating the ticket generation process (e.g., QR generation, server sync)
        delay(3000) 
        onLoadingComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13131A)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = CyanBlue,
            modifier = Modifier.size(64.dp),
            strokeWidth = 6.dp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Đang tạo mã vé...",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Vui lòng đợi trong giây lát,\nhệ thống đang xử lý thông tin đặt vé của bạn.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}
