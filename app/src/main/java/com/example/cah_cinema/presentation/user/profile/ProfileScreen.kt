package com.example.cah_cinema.presentation.user.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cah_cinema.R
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.ui.theme.TextGray
import com.example.cah_cinema.presentation.component.FullScreenLoading
import coil.compose.AsyncImage
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToAllTickets: () -> Unit = {},
    onNavigateToTicketDetail: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // Dialog xác nhận xóa tài khoản
    if (state.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ProfileEvent.CancelDeleteAccount) },
            title = { Text("Xóa tài khoản", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Bạn có chắc muốn xóa tài khoản? Hành động này không thể hoàn tác.",
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onEvent(ProfileEvent.ConfirmDeleteAccount)
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("XÓA", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(ProfileEvent.CancelDeleteAccount) }) {
                    Text("HỦY", color = Color.White.copy(alpha = 0.6f))
                }
            },
            containerColor = Color(0xFF21212B)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ProfileContent(
            state = state,
            onEvent = { event ->
                when (event) {
                    ProfileEvent.ChangePassword -> onNavigateToChangePassword()
                    ProfileEvent.EditProfile -> onNavigateToEditProfile()
                    ProfileEvent.ViewAllTickets -> onNavigateToAllTickets()
                    ProfileEvent.ViewTicketDetail -> onNavigateToTicketDetail()
                    ProfileEvent.Logout -> onLogout()
                    else -> viewModel.onEvent(event)
                }
            },
            onAdminClick = onNavigateToAdmin
        )

        if (state.isLoading) {
            FullScreenLoading()
        }
    }
}

@Composable
fun ProfileContent(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    onAdminClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13131A))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tài khoản",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Đầu trang Hồ sơ
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(2.dp, CyanBlue, CircleShape)
                    .padding(4.dp)
            ) {
                AsyncImage(
                    model = state.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.account_icon),
                    placeholder = painterResource(id = R.drawable.account_icon)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = state.userName,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = state.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
                Text(
                    text = "Chỉnh sửa hồ sơ",
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline,
                        color = CyanBlue
                    ),
                    modifier = Modifier.clickable { onEvent(ProfileEvent.EditProfile) }
                )
            }
        }

        // Thẻ Điểm tích lũy
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, CyanBlue.copy(alpha = 0.5f)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C22))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Điểm tích lũy:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = CyanBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format(Locale.getDefault(), "%,d", state.loyaltyPoints),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, CyanBlue)
                ) {
                    Text(
                        text = state.rank,
                        style = MaterialTheme.typography.labelLarge,
                        color = CyanBlue,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Tiêu đề phần Vé đã đặt
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable { onEvent(ProfileEvent.ViewAllTickets) },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, CyanBlue),
            color = Color.Transparent
        ) {
            Text(
                text = "Vé đã đặt",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        // Thẻ Vé gần đây
        state.recentTicket?.let { ticket ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C22))
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .height(IntrinsicSize.Min)
                ) {
                    AsyncImage(
                        model = ticket.posterUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .width(100.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.cinema),
                        placeholder = painterResource(id = R.drawable.cinema)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = ticket.movieTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            maxLines = 2
                        )
                        TicketInfoItem(icon = Icons.Default.LocationOn, text = ticket.cinemaName)
                        TicketInfoItem(icon = Icons.Default.AccessTime, text = ticket.showTime)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF323232)
                            ) {
                                Text(
                                    text = "Ghế: ${ticket.seat}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Button(
                                onClick = { onEvent(ProfileEvent.ViewTicketDetail) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = "Mã vé",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Phần Menu
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C22))
        ) {
            Column {
                if (state.role == "ROLE_ADMIN") {
                    MenuItem(text = "Admin Panel", textColor = CyanBlue, onClick = onAdminClick)
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                }
                MenuItem(text = "Đổi mật khẩu", onClick = { onEvent(ProfileEvent.ChangePassword) })
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                MenuItem(text = "Đăng xuất", textColor = Color.Red, onClick = { onEvent(ProfileEvent.Logout) })
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                MenuItem(text = "Xoá tài khoản", textColor = Color.Red, onClick = { onEvent(ProfileEvent.DeleteAccount) })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun TicketInfoItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CyanBlue,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = TextGray,
            maxLines = 1
        )
    }
}

@Composable
fun MenuItem(
    text: String,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = if (textColor == Color.Red) Color.Red else Color.White
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    CAH_CinemaTheme {
        ProfileContent(
            state = ProfileState(
                userName = "Hinno",
                email = "example@gmail.com",
                loyaltyPoints = 1367,
                rank = "Hạng vàng",
                recentTicket = TicketInfo(
                    movieTitle = "HẸN EM NGÀY NHẬT THỰC (T13)",
                    cinemaName = "Cinestar Quốc Thanh (TP.HCM)",
                    showTime = "18:20 - 06/04/2026",
                    seat = "A11",
                    posterUrl = ""
                )
            ),
            onEvent = {}
        )
    }
}
