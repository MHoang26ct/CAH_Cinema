package com.example.cah_cinema.presentation.user.auth.ForgotPassword

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.presentation.component.AuthTextField
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun ResetPasswordScreen(
    email: String,
    resetToken: String,
    viewModel: ForgotPasswordViewModel = viewModel(),
    onResetSuccess: () -> Unit = {}
) {
    var confirmPassword by remember { mutableStateOf("") }

    // Sync from navigation
    LaunchedEffect(email, resetToken) {
        viewModel.onEmailChange(email)
        viewModel.resetToken = resetToken
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF13131A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "MẬT KHẨU MỚI",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = CyanBlue,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Vui lòng đặt mật khẩu mới cho tài khoản của bạn",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            AuthTextField(
                label = "Mật khẩu mới",
                value = viewModel.newPassword,
                onValueChange = viewModel::onNewPasswordChange,
                placeholder = "Nhập mật khẩu mới",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                label = "Xác nhận mật khẩu",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Nhập lại mật khẩu mới",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            viewModel.errorMessage?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { viewModel.resetPassword(onResetSuccess) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 40.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = viewModel.newPassword.isNotEmpty() && viewModel.newPassword == confirmPassword && !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "LƯU MẬT KHẨU",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1.5f))
        }
    }
}
