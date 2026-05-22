package com.example.cah_cinema.presentation.user.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.cah_cinema.R
import com.example.cah_cinema.data.model.UpdateProfileRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.presentation.component.AuthTextField
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.util.CloudinaryUploader
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit = {},
    onSaveClick: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember(state.userName) { mutableStateOf(state.userName) }
    var email by remember(state.email) { mutableStateOf(state.email) }
    var phone by remember(state.phone) { mutableStateOf(state.phone) }
    var avatarUrl by remember(state.avatarUrl) { mutableStateOf(state.avatarUrl) }
    var isUploading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Image picker launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            viewModel.viewModelScope.launch {
                val result = CloudinaryUploader.uploadImage(context, it)
                isUploading = false
                result.fold(
                    onSuccess = { url -> avatarUrl = url },
                    onFailure = { e -> errorMsg = "Upload ảnh thất bại: ${e.message}" }
                )
            }
        }
    }

    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            errorMsg = null
        }
    }

    Scaffold(
        containerColor = Color(0xFF13131A),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    modifier = Modifier.size(32.dp).clickable { onBackClick() }
                )
                Text(
                    text = "Chỉnh sửa hồ sơ",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .border(2.dp, CyanBlue, CircleShape)
                    .padding(4.dp)
            ) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.account_icon),
                    placeholder = painterResource(id = R.drawable.account_icon)
                )
                if (isUploading) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanBlue, modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isUploading) "Đang tải ảnh..." else "Thay đổi ảnh đại diện",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isUploading) Color.White.copy(alpha = 0.5f) else CyanBlue,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable(enabled = !isUploading) {
                    imageLauncher.launch("image/*")
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            AuthTextField(
                label = "Tên người dùng",
                value = name,
                onValueChange = { name = it },
                placeholder = "Nhập tên của bạn"
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

            Button(
                onClick = {
                    isSaving = true
                    viewModel.viewModelScope.launch {
                        try {
                            val request = UpdateProfileRequest(
                                name = name.takeIf { it.isNotBlank() },
                                email = email.takeIf { it.isNotBlank() },
                                phone = phone.takeIf { it.isNotBlank() },
                                avatarUrl = avatarUrl.takeIf { it.isNotBlank() }
                            )
                            val response = RetrofitClient.apiService.updateMyProfile(request)
                            isSaving = false
                            if (response.isSuccessful) {
                                // Lưu avatarUrl local để hiển thị ngay cả khi backend clone cũ chưa lưu
                                if (avatarUrl.isNotBlank()) {
                                    RetrofitClient.saveAvatarUrl(avatarUrl)
                                }
                                viewModel.loadProfileData()
                                onSaveClick(name, email, phone)
                            } else {
                                errorMsg = response.body()?.message ?: "Cập nhật thất bại"
                            }
                        } catch (e: Exception) {
                            isSaving = false
                            errorMsg = e.message ?: "Lỗi kết nối"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                shape = RoundedCornerShape(28.dp),
                enabled = !isSaving && !isUploading
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                } else {
                    Text(
                        text = "Lưu thay đổi",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
