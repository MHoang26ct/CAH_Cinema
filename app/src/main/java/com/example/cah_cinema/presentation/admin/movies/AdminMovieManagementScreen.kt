package com.example.cah_cinema.presentation.admin.movies

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.MovieListItem
import com.example.cah_cinema.data.model.UpdateOrCreateMovieRequest
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun AdminMovieManagementScreen(
    viewModel: AdminMovieViewModel = viewModel(),
    onNavigate: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    AdminMovieManagementContent(
        state = state,
        onNavigate = onNavigate,
        onDeleteMovie = { viewModel.deleteMovie(it) },
        onAddClick = { showAddDialog = true }
    )

    if (showAddDialog) {
        AddMovieDialog(
            genres = state.genres,
            onDismiss = { showAddDialog = false },
            onConfirm = { request ->
                viewModel.createMovie(request) {
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun AdminMovieManagementContent(
    state: AdminMovieState,
    onNavigate: (String) -> Unit,
    onDeleteMovie: (Long) -> Unit,
    onAddClick: () -> Unit
) {
    AdminScaffold(
        title = "Quản lý Phim"
    ) { paddingValues ->
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
                    onClick = onAddClick,
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
                // Movie Table Header
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
                        MovieRow(movie, onDelete = { onDeleteMovie(movie.id) })
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieDialog(
    genres: List<com.example.cah_cinema.data.model.Genre>,
    onDismiss: () -> Unit,
    onConfirm: (UpdateOrCreateMovieRequest) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    
    // Release Date Components
    var day by remember { mutableStateOf("16") }
    var month by remember { mutableStateOf("05") }
    var year by remember { mutableStateOf("2026") }
    
    var ageRating by remember { mutableStateOf("T13") }
    var posterUrl by remember { mutableStateOf("") }
    var trailerUrl by remember { mutableStateOf("") }
    var directorName by remember { mutableStateOf("") }
    var actorList by remember { mutableStateOf("") }
    val selectedGenreIds = remember { mutableStateListOf<Long>() }

    var ageRatingExpanded by remember { mutableStateOf(false) }
    val ageRatings = listOf("P", "T13", "T16", "T18")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm phim mới", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AdminTextField(value = title, onValueChange = { title = it }, label = "Tên phim")
                AdminTextField(value = description, onValueChange = { description = it }, label = "Mô tả", singleLine = false)
                AdminTextField(value = duration, onValueChange = { duration = it }, label = "Thời lượng (phút)")
                
                // Ngày khởi chiếu
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ngày khởi chiếu", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AdminTextField(value = day, onValueChange = { if(it.length <= 2) day = it }, label = "Ngày", modifier = Modifier.weight(1f))
                        AdminTextField(value = month, onValueChange = { if(it.length <= 2) month = it }, label = "Tháng", modifier = Modifier.weight(1f))
                        AdminTextField(value = year, onValueChange = { if(it.length <= 4) year = it }, label = "Năm", modifier = Modifier.weight(1.5f))
                    }
                }

                // Độ tuổi (Dropdown)
                Column {
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
                                    onClick = {
                                        ageRating = rating
                                        ageRatingExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                AdminTextField(value = directorName, onValueChange = { directorName = it }, label = "Đạo diễn")
                AdminTextField(value = actorList, onValueChange = { actorList = it }, label = "Diễn viên")
                AdminTextField(value = posterUrl, onValueChange = { posterUrl = it }, label = "Link Poster")
                AdminTextField(value = trailerUrl, onValueChange = { trailerUrl = it }, label = "Link Trailer (YouTube)")
                
                Text("Thể loại", color = Color.White, fontSize = 14.sp)
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    genres.forEach { genre ->
                        FilterChip(
                            selected = selectedGenreIds.contains(genre.id),
                            onClick = {
                                if (selectedGenreIds.contains(genre.id)) selectedGenreIds.remove(genre.id)
                                else selectedGenreIds.add(genre.id)
                            },
                            label = { Text(genre.name) },
                            modifier = Modifier.padding(end = 8.dp),
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
                            title = title,
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
                enabled = title.isNotBlank() && selectedGenreIds.isNotEmpty()
            ) {
                Text("XÁC NHẬN", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY", color = Color.White.copy(alpha = 0.6f)) }
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
fun MovieRow(movie: MovieListItem, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(3f), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(44.dp, 64.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.05f)
            ) {
                // AsyncImage or placeholder
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = movie.title, 
                color = Color.White, 
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
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
        Row(modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { /* Edit */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.6f))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminMovieManagementPreview() {
    CAH_CinemaTheme {
        AdminMovieManagementContent(
            state = AdminMovieState(
                movies = listOf(
                    MovieListItem(1, "HẸN EM NGÀY NHẬT THỰC", 118, "T16", ""),
                    MovieListItem(2, "KUNG FU PANDA 4", 94, "P", ""),
                    MovieListItem(3, "CAPTAIN AMERICA 4", 120, "T13", "")
                ),
                isLoading = false
            ),
            onNavigate = {},
            onDeleteMovie = {},
            onAddClick = {}
        )
    }
}
