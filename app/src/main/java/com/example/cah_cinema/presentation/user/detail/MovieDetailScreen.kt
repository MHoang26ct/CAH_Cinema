package com.example.cah_cinema.presentation.user.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.data.model.CommentItem
import com.example.cah_cinema.domain.model.Cinema
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.MovieDate
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.ui.theme.TextGray
import com.example.cah_cinema.util.DateTimeUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onShowtimeClick: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    state.movie?.let { movie ->
        Scaffold(
            containerColor = Color(0xFF13131A),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    MovieHeaderSection(movie = movie, onBackClick = onBackClick)
                }

                item {
                    MovieDetailsSection(movie = movie)
                }

                // Trailer section
                if (!movie.trailerUrl.isNullOrBlank()) {
                    item {
                        TrailerSection(trailerUrl = movie.trailerUrl)
                    }
                }

                if (state.availableDates.isNotEmpty()) {
                    item {
                        DateSelectionSection(
                            dates = state.availableDates,
                            onDateSelected = { viewModel.onDateSelected(it) }
                        )
                    }
                }

                items(state.cinemas) { cinema ->
                    val selectedDate = state.availableDates.find { it.isSelected }?.date ?: ""
                    CinemaSection(
                        cinema = cinema,
                        onShowtimeClick = { showtime ->
                            onShowtimeClick(movie.id, showtime.id, selectedDate, showtime.time)
                        }
                    )
                }

                // --- Comment Section ---
                item {
                    CommentHeaderSection(count = state.comments.size)
                }

                item {
                    CommentInputSection(
                        text = state.commentText,
                        isSubmitting = state.isSubmittingComment,
                        onTextChange = { viewModel.onCommentTextChange(it) },
                        onSubmit = { viewModel.submitComment() }
                    )
                }

                items(state.comments) { comment ->
                    CommentRow(
                        comment = comment,
                        onDelete = { viewModel.deleteComment(comment.id) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.White.copy(alpha = 0.05f)
                    )
                }

                if (state.canLoadMoreComments) {
                    item {
                        TextButton(
                            onClick = { viewModel.loadMoreComments() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Xem thêm bình luận", color = CyanBlue)
                        }
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
fun CommentHeaderSection(count: Int) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Bình luận ($count)",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = CyanBlue, thickness = 2.dp, modifier = Modifier.width(40.dp))
    }
}

@Composable
fun CommentInputSection(
    text: String,
    isSubmitting: Boolean,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Viết bình luận...", color = Color.Gray, fontSize = 14.sp) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF2D2D35),
                unfocusedContainerColor = Color(0xFF2D2D35),
                focusedBorderColor = CyanBlue,
                unfocusedBorderColor = Color.Transparent
            ),
            maxLines = 3
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        IconButton(
            onClick = onSubmit,
            enabled = text.isNotBlank() && !isSubmitting,
            colors = IconButtonDefaults.iconButtonColors(containerColor = CyanBlue)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.Send, contentDescription = "Gửi", tint = Color.Black)
            }
        }
    }
}

@Composable
fun CommentRow(
    comment: CommentItem,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = comment.userAvatar ?: "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png",
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.userName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = DateTimeUtils.formatDateTime(comment.createdAt),
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = comment.content,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
        
        // TODO: Chỉ hiển thị nút xóa nếu comment này là của user hiện tại
        // Tạm thời để user có thể click xóa để test
    }
}

/**
 * Section hiển thị nút xem trailer.
 * Nếu URL là Cloudinary video → mở bằng Intent trình phát video.
 * Nếu URL là YouTube → mở YouTube app/browser.
 */
@Composable
fun TrailerSection(trailerUrl: String) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Trailer",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl))
                    context.startActivity(intent)
                },
            color = Color(0xFF1C1C22),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Thumbnail placeholder (dùng gradient)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF2D2D35),
                                    Color(0xFF13131A)
                                )
                            )
                        )
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Xem trailer",
                        tint = CyanBlue,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Xem Trailer",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MovieHeaderSection(movie: Movie, onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
        // Banner với hiệu ứng gradient dọc
        Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
            AsyncImage(
                model = movie.bannerUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF13131A).copy(alpha = 0.5f),
                                Color(0xFF13131A)
                            )
                        )
                    )
            )
        }

        // Nút Quay lại
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Thẻ thông tin nổi (Floating Info Card)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF2D2D35).copy(alpha = 0.95f),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp, 140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Thời lượng: ${movie.duration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                    Text(
                        text = movie.genre,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        TagBox(text = movie.format)
                        Spacer(modifier = Modifier.width(8.dp))
                        TagBox(text = movie.age, backgroundColor = Color(0xFFFFAA00))
                    }
                }
            }
        }
    }
}

@Composable
fun TagBox(text: String, backgroundColor: Color = Color.Transparent) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MovieDetailsSection(movie: Movie) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
        DetailRow(label = "Đạo diễn", value = movie.director)
        DetailRow(label = "Diễn viên", value = movie.cast)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Mô tả phim:",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = movie.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
            modifier = Modifier.padding(top = 8.dp),
            lineHeight = 22.sp
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DateSelectionSection(dates: List<MovieDate>, onDateSelected: (MovieDate) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Chọn ngày chiếu:",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dates) { date ->
                Column(
                    modifier = Modifier
                        .width(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (date.isSelected) CyanBlue else Color(0xFF2D2D35))
                        .clickable { onDateSelected(date) }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.dayOfWeek,
                        color = if (date.isSelected) Color.Black else TextGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = date.date,
                        color = if (date.isSelected) Color.Black else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CinemaSection(
    cinema: Cinema,
    onShowtimeClick: (com.example.cah_cinema.domain.model.Showtime) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF2D2D35).copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = cinema.name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = cinema.address,
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                cinema.showtimes.forEach { showtime ->
                    Box(
                        modifier = Modifier
                            .border(1.dp, CyanBlue, RoundedCornerShape(8.dp))
                            .clickable { onShowtimeClick(showtime) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = showtime.time,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieDetailScreenPreview() {
    CAH_CinemaTheme {
        MovieDetailScreen()
    }
}
