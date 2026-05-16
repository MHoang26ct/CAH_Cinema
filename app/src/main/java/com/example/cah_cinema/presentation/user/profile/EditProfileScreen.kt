package com.example.cah_cinema.presentation.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cah_cinema.R
import com.example.cah_cinema.presentation.component.AuthTextField
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit = {},
    onChangeAvatarClick: () -> Unit = {},
    onSaveClick: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    
    var name by remember(state.userName) { mutableStateOf(state.userName) }
    var email by remember(state.email) { mutableStateOf(state.email) }
    var phone by remember { mutableStateOf("0919492662") } // Số điện thoại ví dụ, lý tưởng nhất là lấy từ state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13131A))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Thanh tiêu đề
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
                text = "Chỉnh sửa hồ sơ",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Phần Ảnh đại diện
        Box(
            modifier = Modifier
                .size(100.dp)
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
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Thay đổi ảnh đại diện",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = CyanBlue,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier.clickable { onChangeAvatarClick() }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Các trường nhập liệu
        AuthTextField(
            label = "Tên người dùng",
            value = name,
            onValueChange = { name = it },
            placeholder = "Hinno"
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            placeholder = "example@gmail.com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            label = "Số điện thoại",
            value = phone,
            onValueChange = { phone = it },
            placeholder = "0123456789",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(56.dp))

        // Nút Lưu
        Button(
            onClick = { onSaveClick(name, email, phone) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Lưu thay đổi",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun EditProfileScreenPreview() {
    CAH_CinemaTheme {
        EditProfileScreen(viewModel = ProfileViewModel())
    }
}
