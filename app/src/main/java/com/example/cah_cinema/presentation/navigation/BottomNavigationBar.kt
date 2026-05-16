package com.example.cah_cinema.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cah_cinema.R
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.ui.theme.TextGray

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onCinemaClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val screens = listOf(
        Screen.Home.route,
        Screen.Cinema.route,
        Screen.Notification.route,
        Screen.Profile.route
    )
    val selectedIndex = screens.indexOf(currentRoute).coerceAtLeast(0)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        color = Color(0xFF1C1C24),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val totalWidth = this.maxWidth
            val itemWidth = totalWidth / 4
            val indicatorSize = 60.dp

            // Logic cho chỉ báo trượt (Sliding Indicator)
            val indicatorOffset by animateDpAsState(
                targetValue = (itemWidth * selectedIndex) + (itemWidth / 2) - (indicatorSize / 2),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "indicatorOffset"
            )

            // Bong bóng nền di động
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .align(Alignment.CenterStart)
                    .size(indicatorSize)
                    .background(color = CyanBlue, shape = CircleShape)
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NavigationItem(
                    isSelected = currentRoute == Screen.Home.route,
                    iconRes = R.drawable.home_icon,
                    contentDescription = "Home",
                    onClick = onHomeClick,
                )

                NavigationItem(
                    isSelected = currentRoute == Screen.Cinema.route,
                    iconRes = R.drawable.ticket_icon,
                    contentDescription = "Cinema",
                    onClick = onCinemaClick,
                )

                NavigationItem(
                    isSelected = currentRoute == Screen.Notification.route,
                    iconRes = R.drawable.notification_icon,
                    contentDescription = "Notification",
                    onClick = onNotificationClick,
                )

                NavigationItem(
                    isSelected = currentRoute == Screen.Profile.route,
                    iconRes = R.drawable.account_icon,
                    contentDescription = "Profile",
                    onClick = onProfileClick,
                )
            }
        }
    }
}

@Composable
fun NavigationItem(
    isSelected: Boolean,
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.9f
            isSelected -> 1.15f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale",
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextGray,
        label = "iconColor",
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(if (isSelected) 30.dp else 28.dp),
        )
    }
}
