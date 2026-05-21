package com.example.cah_cinema.presentation.admin.showtime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.util.DateTimeUtils
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun AdminShowtimeScreen(
    viewModel: AdminShowtimeViewModel = viewModel(),
    onNavigate: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingShowtimePair by remember { mutableStateOf<Pair<Long, ShowtimeInfo>?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    AdminShowtimeContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigate = onNavigate,
        onDeleteShowtime = { viewModel.deleteShowtime(it) },
        onAddClick = { showAddDialog = true },
        onEditClick = { movieId, info -> editingShowtimePair = movieId to info }
    )

    if (showAddDialog) {
        AddShowtimeDialog(
            movies = state.movies,
            cinemas = state.cinemas,
            rooms = state.rooms,
            onCinemaSelected = { viewModel.loadRooms(it) },
            onDismiss = { showAddDialog = false },
            onConfirm = { request ->
                viewModel.createShowtime(request) {
                    showAddDialog = false
                }
            }
        )
    }

    editingShowtimePair?.let { pair ->
        EditShowtimeDialog(
            info = pair.second,
            movieId = pair.first,
            movies = state.movies,
            cinemas = state.cinemas,
            rooms = state.rooms,
            onCinemaSelected = { viewModel.loadRooms(it) },
            onDismiss = { editingShowtimePair = null },
            onConfirm = { request ->
                viewModel.updateShowtime(request) {
                    editingShowtimePair = null
                }
            }
        )
    }
}

@Composable
fun AdminShowtimeContent(
    state: AdminShowtimeState,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    onDeleteShowtime: (Long) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Long, ShowtimeInfo) -> Unit = { _, _ -> }
) {
    AdminScaffold(
        title = "Quản lý Lịch chiếu",
        snackbarHostState = snackbarHostState
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
                    text = "Lịch chiếu hôm nay",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TẠO LỊCH", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading && state.showtimesByMovie.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanBlue)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.showtimesByMovie.forEach { movieItem ->
                        item {
                            Text(
                                text = movieItem.movie.title,
                                color = CyanBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(movieItem.showtimes) { showtime ->
                            ShowtimeItem(
                                showtime = showtime, 
                                onEdit = { onEditClick(movieItem.movie.id, showtime) },
                                onDelete = { onDeleteShowtime(showtime.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditShowtimeDialog(
    info: ShowtimeInfo,
    movieId: Long,
    movies: List<MovieListItem>,
    cinemas: List<CinemaItem>,
    rooms: List<RoomItem>,
    onCinemaSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (UpdateShowtimeRequest) -> Unit
) {
    // Initial values from info
    var selectedMovie by remember { mutableStateOf(movies.find { it.id == movieId }) }
    var selectedCinema by remember { mutableStateOf<CinemaItem?>(null) }
    var selectedRoom by remember { mutableStateOf<RoomItem?>(null) }
    
    // Time components safety check
    val isoStart = info.startTime
    val startYearVal = if (isoStart.length >= 4) isoStart.substring(0, 4) else "2026"
    val startMonthVal = if (isoStart.length >= 7) isoStart.substring(5, 7) else "01"
    val startDayVal = if (isoStart.length >= 10) isoStart.substring(8, 10) else "01"
    val startHourVal = if (isoStart.length >= 13) isoStart.substring(11, 13) else "00"
    val startMinVal = if (isoStart.length >= 16) isoStart.substring(14, 16) else "00"
    
    var day by remember { mutableStateOf(startDayVal) }
    var month by remember { mutableStateOf(startMonthVal) }
    var year by remember { mutableStateOf(startYearVal) }
    var startHour by remember { mutableStateOf(startHourVal) }
    var startMinute by remember { mutableStateOf(startMinVal) }

    val isoEnd = info.endTime
    var endHour by remember { mutableStateOf(if (isoEnd.length >= 13) isoEnd.substring(11, 13) else "00") }
    var endMinute by remember { mutableStateOf(if (isoEnd.length >= 16) isoEnd.substring(14, 16) else "00") }
    
    var format by remember { mutableStateOf(info.format) }
    var price by remember { mutableStateOf(info.basePrice.toInt().toString()) }
    var status by remember { mutableStateOf(info.status) }

    var movieExpanded by remember { mutableStateOf(false) }
    var cinemaExpanded by remember { mutableStateOf(false) }
    var roomExpanded by remember { mutableStateOf(false) }
    var formatExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sửa lịch chiếu", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CHỌN PHIM
                AdminDropdownSelector(
                    label = "Phim",
                    selectedValue = selectedMovie?.title ?: "Chọn phim",
                    expanded = movieExpanded,
                    onExpandedChange = { movieExpanded = it },
                    items = movies,
                    itemLabel = { it.title },
                    onItemSelected = { selectedMovie = it }
                )

                // STATUS
                AdminDropdownSelector(
                    label = "Trạng thái",
                    selectedValue = status,
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = it },
                    items = listOf("AVAILABLE", "SOLD_OUT", "HIDDEN"),
                    itemLabel = { it },
                    onItemSelected = { status = it }
                )

                // NGÀY THÁNG
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ngày chiếu", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TimeInputField(value = day, onValueChange = { if(it.length <= 2) day = it }, label = "Ngày", modifier = Modifier.weight(1f))
                        TimeInputField(value = month, onValueChange = { if(it.length <= 2) month = it }, label = "Tháng", modifier = Modifier.weight(1f))
                        TimeInputField(value = year, onValueChange = { if(it.length <= 4) year = it }, label = "Năm", modifier = Modifier.weight(1.5f))
                    }
                }

                // GIỜ
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Bắt đầu", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TimeInputField(value = startHour, onValueChange = { if(it.length <= 2) startHour = it }, label = "Giờ", modifier = Modifier.weight(1f))
                            TimeInputField(value = startMinute, onValueChange = { if(it.length <= 2) startMinute = it }, label = "Phút", modifier = Modifier.weight(1f))
                        }
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Kết thúc", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TimeInputField(value = endHour, onValueChange = { if(it.length <= 2) endHour = it }, label = "Giờ", modifier = Modifier.weight(1f))
                            TimeInputField(value = endMinute, onValueChange = { if(it.length <= 2) endMinute = it }, label = "Phút", modifier = Modifier.weight(1f))
                        }
                    }
                }

                // CHỌN ĐỊNH DẠNG
                AdminDropdownSelector(
                    label = "Định dạng",
                    selectedValue = format,
                    expanded = formatExpanded,
                    onExpandedChange = { formatExpanded = it },
                    items = listOf("2D", "3D", "IMAX"),
                    itemLabel = { it },
                    onItemSelected = { format = it }
                )
                
                OutlinedTextField(
                    value = price, 
                    onValueChange = { price = it }, 
                    label = { Text("Giá vé cơ bản") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = CyanBlue,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedBorderColor = CyanBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fDay = day.padStart(2, '0')
                    val fMonth = month.padStart(2, '0')
                    val sTime = "$year-$fMonth-${fDay}T${startHour.padStart(2, '0')}:${startMinute.padStart(2, '0')}:00"
                    val eTime = "$year-$fMonth-${fDay}T${endHour.padStart(2, '0')}:${endMinute.padStart(2, '0')}:00"

                    onConfirm(
                        UpdateShowtimeRequest(
                            showtimeId = info.id,
                            movieId = selectedMovie?.id ?: movieId,
                            roomId = info.roomId ?: 0,
                            format = format,
                            startTime = sTime,
                            endTime = eTime,
                            basePrice = price.toDoubleOrNull() ?: info.basePrice,
                            status = status
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue)
            ) {
                Text("LƯU", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY", color = Color.White.copy(alpha = 0.6f)) }
        },
        containerColor = Color(0xFF21212B)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShowtimeDialog(
    movies: List<MovieListItem>,
    cinemas: List<CinemaItem>,
    rooms: List<RoomItem>,
    onCinemaSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (CreateShowtimeRequest) -> Unit
) {
    var selectedMovie by remember { mutableStateOf<MovieListItem?>(null) }
    var selectedCinema by remember { mutableStateOf<CinemaItem?>(null) }
    var selectedRoom by remember { mutableStateOf<RoomItem?>(null) }
    
    // Time components - use current date
    var day by remember { mutableStateOf(DateTimeUtils.getTodayDay()) }
    var month by remember { mutableStateOf(DateTimeUtils.getTodayMonth()) }
    var year by remember { mutableStateOf(DateTimeUtils.getTodayYear()) }
    var startHour by remember { mutableStateOf("18") }
    var startMinute by remember { mutableStateOf("00") }
    var endHour by remember { mutableStateOf("20") }
    var endMinute by remember { mutableStateOf("00") }
    
    var format by remember { mutableStateOf("2D") }
    var price by remember { mutableStateOf("45000") }

    var movieExpanded by remember { mutableStateOf(false) }
    var cinemaExpanded by remember { mutableStateOf(false) }
    var roomExpanded by remember { mutableStateOf(false) }
    var formatExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo lịch chiếu mới", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CHỌN PHIM
                AdminDropdownSelector(
                    label = "Phim",
                    selectedValue = selectedMovie?.title ?: "Chọn phim",
                    expanded = movieExpanded,
                    onExpandedChange = { movieExpanded = it },
                    items = movies,
                    itemLabel = { it.title },
                    onItemSelected = { selectedMovie = it }
                )

                // CHỌN RẠP
                AdminDropdownSelector(
                    label = "Rạp",
                    selectedValue = selectedCinema?.name ?: "Chọn rạp",
                    expanded = cinemaExpanded,
                    onExpandedChange = { cinemaExpanded = it },
                    items = cinemas,
                    itemLabel = { it.name },
                    onItemSelected = { 
                        selectedCinema = it
                        selectedRoom = null // Reset room when cinema changes
                        onCinemaSelected(it.id) 
                    }
                )
                
                // CHỌN PHÒNG
                AdminDropdownSelector(
                    label = "Phòng chiếu",
                    selectedValue = selectedRoom?.name ?: "Chọn phòng",
                    expanded = roomExpanded,
                    onExpandedChange = { roomExpanded = it },
                    items = rooms,
                    itemLabel = { it.name },
                    onItemSelected = { selectedRoom = it },
                    enabled = selectedCinema != null
                )

                // NGÀY THÁNG
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ngày chiếu", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TimeInputField(value = day, onValueChange = { if(it.length <= 2) day = it }, label = "Ngày", modifier = Modifier.weight(1f))
                        TimeInputField(value = month, onValueChange = { if(it.length <= 2) month = it }, label = "Tháng", modifier = Modifier.weight(1f))
                        TimeInputField(value = year, onValueChange = { if(it.length <= 4) year = it }, label = "Năm", modifier = Modifier.weight(1.5f))
                    }
                }

                // GIỜ BẮT ĐẦU & KẾT THÚC
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Bắt đầu", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TimeInputField(value = startHour, onValueChange = { if(it.length <= 2) startHour = it }, label = "Giờ", modifier = Modifier.weight(1f))
                            TimeInputField(value = startMinute, onValueChange = { if(it.length <= 2) startMinute = it }, label = "Phút", modifier = Modifier.weight(1f))
                        }
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Kết thúc", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TimeInputField(value = endHour, onValueChange = { if(it.length <= 2) endHour = it }, label = "Giờ", modifier = Modifier.weight(1f))
                            TimeInputField(value = endMinute, onValueChange = { if(it.length <= 2) endMinute = it }, label = "Phút", modifier = Modifier.weight(1f))
                        }
                    }
                }

                // CHỌN ĐỊNH DẠNG
                AdminDropdownSelector(
                    label = "Định dạng",
                    selectedValue = format,
                    expanded = formatExpanded,
                    onExpandedChange = { formatExpanded = it },
                    items = listOf("2D", "3D", "IMAX"),
                    itemLabel = { it },
                    onItemSelected = { format = it }
                )
                
                OutlinedTextField(
                    value = price, 
                    onValueChange = { price = it }, 
                    label = { Text("Giá vé cơ bản") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = CyanBlue,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        focusedBorderColor = CyanBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedMovie != null && selectedRoom != null) {
                        val fDay = day.padStart(2, '0')
                        val fMonth = month.padStart(2, '0')
                        val sTime = "$year-$fMonth-${fDay}T${startHour.padStart(2, '0')}:${startMinute.padStart(2, '0')}:00"
                        val eTime = "$year-$fMonth-${fDay}T${endHour.padStart(2, '0')}:${endMinute.padStart(2, '0')}:00"

                        onConfirm(
                            CreateShowtimeRequest(
                                movieId = selectedMovie!!.id,
                                roomId = selectedRoom!!.id,
                                format = format,
                                startTime = sTime,
                                endTime = eTime,
                                basePrice = price.toDoubleOrNull() ?: 45000.0
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = selectedMovie != null && selectedRoom != null
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AdminDropdownSelector(
    label: String,
    selectedValue: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<T>,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    enabled: Boolean = true
) {
    Column {
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) onExpandedChange(it) }
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = if (enabled) Color.White else Color.White.copy(alpha = 0.2f),
                    focusedBorderColor = CyanBlue,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    disabledBorderColor = Color.White.copy(alpha = 0.05f)
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.background(Color(0xFF2D2D35))
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(itemLabel(item), color = Color.White) },
                        onClick = {
                            onItemSelected(item)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TimeInputField(
    value: String, 
    onValueChange: (String) -> Unit, 
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.all { char -> char.isDigit() }) onValueChange(it) },
        label = { Text(label, fontSize = 10.sp) },
        modifier = modifier,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = CyanBlue,
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
        )
    )
}

@Composable
fun ShowtimeItem(showtime: ShowtimeInfo, onEdit: () -> Unit = {}, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1C1C22),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${showtime.startTime.substringAfter("T").substring(0, 5)} - ${showtime.endTime.substringAfter("T").substring(0, 5)} | Phòng: ${showtime.roomName}",
                        color = Color.White, 
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Định dạng: ${showtime.format} | Trạng thái: ${showtime.status}", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.4f))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminShowtimePreview() {
    CAH_CinemaTheme {
        AdminShowtimeContent(
            state = AdminShowtimeState(
                showtimesByMovie = listOf(
                    CinemaShowtimeItem(
                        movie = MovieInfo(1, "HẸN EM NGÀY NHẬT THỰC", "", "T16"),
                        showtimes = listOf(
                            ShowtimeInfo(id = 101, movieId = 1, roomId = 1, startTime = "2026-05-16T18:20:00", endTime = "2026-05-16T20:18:00", format = "2D", basePrice = 45000.0, status = "AVAILABLE", roomName = "Phòng 01"),
                            ShowtimeInfo(id = 102, movieId = 1, roomId = 2, startTime = "2026-05-16T20:30:00", endTime = "2026-05-16T22:28:00", format = "2D", basePrice = 45000.0, status = "AVAILABLE", roomName = "Phòng 02")
                        )
                    )
                ),
                isLoading = false
            ),
            snackbarHostState = SnackbarHostState(),
            onNavigate = {},
            onDeleteShowtime = {},
            onAddClick = {}
        )
    }
}
