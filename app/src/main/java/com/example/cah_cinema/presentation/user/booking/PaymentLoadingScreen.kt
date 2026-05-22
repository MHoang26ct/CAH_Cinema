package com.example.cah_cinema.presentation.user.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    // Chờ tối thiểu 1.5s để UX mượt, sau đó navigate
    LaunchedEffect(Unit) {
        delay(1500)
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
            text = "Đặt vé thành công!",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Đang tạo mã vé của bạn...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}
