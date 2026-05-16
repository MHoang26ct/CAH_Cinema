package com.example.cah_cinema.presentation.user.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cah_cinema.R
import com.example.cah_cinema.ui.theme.CyanBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNextScreen: () -> Unit
) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    val textSlide = remember { Animatable(20f) }
    
    // Background pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(key1 = true) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(1000)
            )
        }
        launch {
            textSlide.animateTo(
                targetValue = 0f,
                animationSpec = tween(1200, easing = EaseOutCubic)
            )
        }
        
        delay(2500)
        onNextScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13131A)),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background glow
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.15f)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CyanBlue, Color.Transparent),
                    center = center,
                    radius = size.minDimension * pulseScale
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-20).dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ticket_icon),
                contentDescription = "App Logo",
                tint = CyanBlue,
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = textSlide.value.dp)
                    .alpha(alpha.value)
            ) {
                Text(
                    text = "CAH CINEMA",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 6.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "EXPERIENCE THE MAGIC",
                    color = CyanBlue.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
            }
        }
        
        // Bottom loading indicator
        LinearProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.4f)
                .padding(bottom = 64.dp)
                .height(2.dp)
                .alpha(alpha.value),
            color = CyanBlue,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun LinearProgressIndicator(
    modifier: Modifier,
    color: Color,
    trackColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Box(
        modifier = modifier
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(color)
        )
    }
}
