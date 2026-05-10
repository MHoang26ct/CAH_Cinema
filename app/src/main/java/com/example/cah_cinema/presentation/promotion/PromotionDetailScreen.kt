package com.example.cah_cinema.presentation.promotion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.ui.theme.TextGray

@Composable
fun PromotionDetailScreen(
    viewModel: PromotionDetailViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF13131A),
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = state.promotion?.title ?: "",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            }
        }
    ) { paddingValues ->
        state.promotion?.let { promotion ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    AsyncImage(
                        model = promotion.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = promotion.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = promotion.description,
                        color = TextGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (promotion.conditions.isNotEmpty()) {
                    item {
                        Text(
                            text = "Điều kiện",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(promotion.conditions) { condition ->
                        BulletItem(text = condition)
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }

                if (promotion.notes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Lưu ý",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(promotion.notes) { note ->
                        BulletItem(text = note)
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun BulletItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = "•", color = Color.White, modifier = Modifier.padding(end = 8.dp))
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}
