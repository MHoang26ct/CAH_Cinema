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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.presentation.component.AuthTextField
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun OtpVerificationScreen(
    email: String,
    viewModel: ForgotPasswordViewModel = viewModel(),
    onOtpVerified: (String) -> Unit = {}
) {
    // Sync email from navigation
    LaunchedEffect(email) {
        viewModel.onEmailChange(email)
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
                text = "XÁC THỰC OTP",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = CyanBlue,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Vui lòng nhập mã OTP đã được gửi đến email:\n$email",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            AuthTextField(
                label = "Mã OTP",
                value = viewModel.otp,
                onValueChange = viewModel::onOtpChange,
                placeholder = "Nhập 6 số",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            viewModel.errorMessage?.let {
                Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.verifyOtp(onOtpVerified) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 40.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = viewModel.otp.length == 6 && !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "XÁC NHẬN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = { viewModel.sendOtp {} }) {
                Text(
                    text = "Gửi lại mã",
                    color = CyanBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1.5f))
        }
    }
}
