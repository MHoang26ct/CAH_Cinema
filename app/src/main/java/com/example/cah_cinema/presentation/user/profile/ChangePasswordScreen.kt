package com.example.cah_cinema.presentation.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.cah_cinema.data.model.ChangePasswordRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.presentation.component.AuthTextField
import com.example.cah_cinema.ui.theme.CyanBlue
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: (String, String, String) -> Unit = { _, _, _ -> }
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var successMsg by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            errorMsg = null
        }
    }
    LaunchedEffect(successMsg) {
        successMsg?.let {
            snackbarHostState.showSnackbar(it)
            successMsg = null
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
                .padding(16.dp)
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
                    text = "Đổi mật khẩu",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                label = "Mật khẩu hiện tại",
                value = currentPassword,
                onValueChange = { currentPassword = it },
                placeholder = "••••••••••••",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                label = "Mật khẩu mới",
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = "••••••••••••",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                label = "Nhập lại mật khẩu mới",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "••••••••••••",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    when {
                        currentPassword.isBlank() -> { errorMsg = "Vui lòng nhập mật khẩu hiện tại" }
                        newPassword.length < 6 -> { errorMsg = "Mật khẩu mới phải có ít nhất 6 ký tự" }
                        newPassword != confirmPassword -> { errorMsg = "Mật khẩu xác nhận không khớp" }
                        else -> {
                            isLoading = true
                            scope.launch {
                                try {
                                    val response = RetrofitClient.apiService.changePassword(
                                        ChangePasswordRequest(
                                            oldPassword = currentPassword,
                                            newPassword = newPassword
                                        )
                                    )
                                    isLoading = false
                                    if (response.isSuccessful && response.body()?.code == 200) {
                                        successMsg = "Đổi mật khẩu thành công!"
                                        onSaveClick(currentPassword, newPassword, confirmPassword)
                                    } else {
                                        errorMsg = response.body()?.message ?: "Đổi mật khẩu thất bại"
                                    }
                                } catch (e: Exception) {
                                    isLoading = false
                                    errorMsg = e.message ?: "Lỗi kết nối"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                shape = RoundedCornerShape(28.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
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
