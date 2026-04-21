package com.example.cah_cinema.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.cah_cinema.R

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat)),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),

    // 2. Dùng cho "MUA VÉ", "Tài khoản"
    headlineMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat)),
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),

    // 3. Dùng cho "Sắp chiếu", "Nước uống"
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat)),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),

    // 4. Dùng cho tên phim, tên rạp, tên món ăn
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat)),
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),

    // 5. Dùng cho nội dung nhập liệu, text bình thường
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),

    // 6. Dùng cho đoạn văn dài (Màn hình ưu đãi)
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter)),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),

    // 7. Dùng cho chữ trong nút "THANH TOÁN", "Đặt vé"
    labelLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat)),
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),

    // 8. Dùng cho địa chỉ rạp, giờ chiếu (Chữ nhỏ màu xám)
    labelSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter)),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )
)
