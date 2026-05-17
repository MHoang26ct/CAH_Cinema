package com.example.cah_cinema.presentation.admin.movies

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.data.model.Genre
import com.example.cah_cinema.data.model.MovieDetail
import com.example.cah_cinema.data.model.MovieListItem
import com.example.cah_cinema.data.model.UpdateOrCreateMovieRequest
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun AdminMovieManagementScreen(
    viewModel: AdminMovieViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMovie by remember { mutableStateOf<MovieListItem?>(null) }

    // Snackbar cho success/error
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    AdminScaffold(title = "Quản lý Phim", snackbarHostState = snackbarHostState) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Danh sách phim (${state.movies.size})",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("THÊM PHIM MỚI", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading && state.movies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanBlue)
                }
            } else {
                // Header bảng
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C22), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PHIM", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(3f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text("THỜI LƯỢNG", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text("ĐỘ TUỔI", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text("THAO TÁC", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .background(Color(0xFF1C1C22).copy(alpha = 0.5f))
                ) {
                    items(state.movies) { movie ->
                        MovieRow(
                            movie = movie,
                            onEdit = { editingMovie = movie },
                            onDelete = { viewModel.deleteMovie(movie.id) }
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        }
    }

    // Dialog thêm phim
    if (showAddDialog) {
        MovieFormDialog(
            title = "Thêm phim mới",
            genres = state.genres,
            isUploading = state.isUploading,
            onDismiss = { showAddDialog = false },
            onUploadImage = { context, uri, callback ->
                viewModel.uploadPosterImage(context, uri, callback)
            },
            onConfirm = { request ->
                viewModel.createMovie(request) { showAddDialog = false }
            }
        )
    }

    // Dialog sửa phim
    editingMovie?.let { movie ->
        MovieFormDialog(
            title = "Sửa phim",
            genres = state.genres,
            isUploading = state.isUploading,
            initialData = movie,
            onDismiss = { editingMovie = null },
            onUploadImage = { context, uri, callback ->
                viewModel.uploadPosterImage(context, uri, callback)
            },
            onConfirm = { request ->
                viewModel.updateMovie(movie.id, request) { editingMovie = null }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MovieFormDialog(
    title: String,
    genres: List<Genre>,
    isUploading: Boolean,
    initialData: MovieListItem? = null,
    onDismiss: () -> Unit,
    onUploadImage: (android.content.Context, Uri, (String?) -> Unit) -> Unit,
    onConfirm: (UpdateOrCreateMovieRequest) -> Unit
) {
    val context = LocalContext.current

    var movieTitle by remember { mutableStateOf(initialData?.title ?: "") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(initialData?.duration?.toString() ?: "") }
    var day by remember { mutableStateOf("01") }
    var month by remember { mutableStateOf("01") }
    var year by remember { mutableStateOf("2026") }
    var ageRating by remember { mutableStateOf(initialData?.ageRating ?: "T13") }
    var posterUrl by remember { mutableStateOf(initialData?.posterUrl ?: "") }
    var trailerUrl by remember { mutableStateOf("") }
    var directorName by remember { mutableStateOf("") }
    var actorList by remember { mutableStateOf("") }
    val selectedGenreIds = remember { mutableStateListOf<Long>() }
    var ageRatingExpanded by remember { mutableStateOf(false) }
    val ageRatings = listOf("P", "T13", "T16", "T18")

    // Image picker launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onUploadImage(context, it) { url ->
                if (url != null) posterUrl = url
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminTextField(value = movieTitle, onValueChange = { movieTitle = it }, label = "Tên phim")
                AdminTextField(value = description, onValueChange = { description = it }, label = "Mô tả", singleLine = false)
                AdminTextField(value = duration, onValueChange = { duration = it }, label = "Thời lượng (phút)")

                // Ngày khởi chiếu
                Text("Ngày khởi chiếu", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(value = day, onValueChange = { if (it.length <= 2) day = it }, label = "Ngày", modifier = Modifier.weight(1f))
                    AdminTextField(value = month, onValueChange = { if (it.length <= 2) month = it }, label = "Tháng", modifier = Modifier.weight(1f))
                    AdminTextField(value = year, onValueChange = { if (it.length <= 4) year = it }, label = "Năm", modifier = Modifier.weight(1.5f))
                }

                // Độ tuổi dropdown
                Text("Độ tuổi", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                ExposedDropdownMenuBox(
                    expanded = ageRatingExpanded,
                    onExpandedChange = { ageRatingExpanded = !ageRatingExpanded }
                ) {
                    OutlinedTextField(
                        value = ageRating,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ageRatingExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyanBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = ageRatingExpanded,
                        onDismissRequest = { ageRatingExpanded = false },
                        modifier = Modifier.background(Color(0xFF2D2D35))
                    ) {
                        ageRatings.forEach { rating ->
                            DropdownMenuItem(
                                text = { Text(rating, color = Color.White) },
                                onClick = { ageRating = rating; ageRatingExpanded = false }
                            )
                        }
                    }
                }

                AdminTextField(value = directorName, onValueChange = { directorName = it }, label = "Đạo diễn")
                AdminTextField(value = actorList, onValueChange = { actorList = it }, label = "Diễn viên")

                // Poster URL + nút upload ảnh
                Text("Poster", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = posterUrl,
                        onValueChange = { posterUrl = it },
                        label = { Text("URL Poster") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = CyanBlue,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedBorderColor = CyanBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                    // Nút chọn ảnh từ thiết bị → upload Cloudinary
                    IconButton(
                        onClick = { imageLauncher.launch("image/*") },
                        enabled = !isUploading
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = CyanBlue,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Chọn ảnh",
                                tint = CyanBlue
                            )
                        }
                    }
                }

                // Preview poster nếu có URL
                if (posterUrl.isNotEmpty()) {
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = "Poster preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                AdminTextField(value = trailerUrl, onValueChange = { trailerUrl = it }, label = "Link Trailer (YouTube)")

                // Thể loại
                Text("Thể loại", color = Color.White, fontSize = 14.sp)
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    genres.forEach { genre ->
                        FilterChip(
                            selected = selectedGenreIds.contains(genre.id),
                            onClick = {
                                if (selectedGenreIds.contains(genre.id)) selectedGenreIds.remove(genre.id)
                                else selectedGenreIds.add(genre.id)
                            },
                            label = { Text(genre.name) },
                            modifier = Modifier.padding(end = 8.dp, bottom = 4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanBlue,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val formattedDate = "$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}"
                    onConfirm(
                        UpdateOrCreateMovieRequest(
                            title = movieTitle,
                            description = description,
                            duration = duration.toIntOrNull() ?: 120,
                            releaseDate = formattedDate,
                            ageRating = ageRating,
                            posterUrl = posterUrl,
                            trailerUrl = trailerUrl,
                            directorName = directorName,
                            actorList = actorList,
                            genreIdList = selectedGenreIds.toList()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = movieTitle.isNotBlank() && selectedGenreIds.isNotEmpty() && !isUploading
            ) {
                Text("XÁC NHẬN", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("HỦY", color = Color.White.copy(alpha = 0.6f))
            }
        },
        containerColor = Color(0xFF21212B)
    )
}

@Composable
fun AdminTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = CyanBlue,
            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
            focusedBorderColor = CyanBlue,
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
        )
    )
}

@Composable
fun MovieRow(
    movie: MovieListItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(3f), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp, 64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = movie.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
        }
        Text(
            text = "${movie.duration} phút",
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = movie.ageRating,
            color = CyanBlue,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Row(modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = CyanBlue.copy(alpha = 0.8f))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }
}
