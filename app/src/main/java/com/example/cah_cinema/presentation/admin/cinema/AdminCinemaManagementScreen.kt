package com.example.cah_cinema.presentation.admin.cinema

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.data.model.CinemaItem
import com.example.cah_cinema.data.model.RoomItem
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.navigation.Screen
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun AdminCinemaManagementScreen(
    viewModel: AdminCinemaViewModel = viewModel(),
    onNavigate: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showAddRoomDialog by remember { mutableStateOf<Long?>(null) } // CinemaId
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

    AdminCinemaManagementContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigate = onNavigate,
        onDeleteCinema = { viewModel.deleteCinema(it) },
        onAddClick = { showAddDialog = true },
        onAddRoomClick = { showAddRoomDialog = it },
        onDeleteRoom = { roomId, cinemaId -> viewModel.deleteRoom(roomId, cinemaId) },
        onManageSeats = { roomId -> onNavigate(Screen.AdminSeatManagement.createRoute(roomId)) }
    )

    if (showAddDialog) {
        AddCinemaDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, address, hotline, imageUrl ->
                viewModel.createCinema(name, address, hotline, imageUrl) {
                    showAddDialog = false
                }
            }
        )
    }

    if (showAddRoomDialog != null) {
        AddRoomDialog(
            onDismiss = { showAddRoomDialog = null },
            onConfirm = { roomName ->
                viewModel.createRoom(showAddRoomDialog!!, roomName) {
                    showAddRoomDialog = null
                }
            }
        )
    }
}

@Composable
fun AdminCinemaManagementContent(
    state: AdminCinemaState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onNavigate: (String) -> Unit,
    onDeleteCinema: (Long) -> Unit,
    onAddClick: () -> Unit,
    onAddRoomClick: (Long) -> Unit,
    onDeleteRoom: (Long, Long) -> Unit = { _, _ -> },
    onManageSeats: (Long) -> Unit
) {
    AdminScaffold(
        title = "Quản lý Rạp",
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
                    text = "Danh sách cụm rạp (${state.cinemas.size})",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
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
                    Text("THÊM RẠP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading && state.cinemas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanBlue)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.cinemas) { cinema ->
                        CinemaCard(
                            cinema = cinema, 
                            rooms = state.roomsByCinema[cinema.id] ?: emptyList(),
                            onDelete = { onDeleteCinema(cinema.id) },
                            onAddRoom = { onAddRoomClick(cinema.id) },
                            onDeleteRoom = { roomId -> onDeleteRoom(roomId, cinema.id) },
                            onManageSeats = onManageSeats
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddRoomDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var roomName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm phòng chiếu mới", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = roomName, 
                    onValueChange = { roomName = it }, 
                    label = { Text("Tên phòng (ví dụ: Phòng 01)") },
                    modifier = Modifier.fillMaxWidth(),
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
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(roomName) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = roomName.isNotBlank()
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
fun AddCinemaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var hotline by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm rạp mới", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Tên rạp") },
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
                OutlinedTextField(
                    value = address, 
                    onValueChange = { address = it }, 
                    label = { Text("Địa chỉ") },
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
                OutlinedTextField(
                    value = hotline, 
                    onValueChange = { hotline = it }, 
                    label = { Text("Hotline") },
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
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL ảnh rạp (tuỳ chọn)") },
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
                onClick = { onConfirm(name, address, hotline, imageUrl.ifBlank { null }) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue)
            ) {
                Text("XÁC NHẬN", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY") }
        },
        containerColor = Color(0xFF21212B)
    )
}

@Composable
fun CinemaCard(
    cinema: CinemaItem, 
    rooms: List<RoomItem>,
    onDelete: () -> Unit,
    onAddRoom: () -> Unit,
    onDeleteRoom: (Long) -> Unit = {},
    onManageSeats: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        color = Color(0xFF1C1C22),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    color = CyanBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (!cinema.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = cinema.imageUrl,
                            contentDescription = cinema.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(26.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(cinema.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(cinema.address, color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyMedium)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { /* Edit */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.4f))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                    }
                }
            }

            if (expanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PHÒNG CHIẾU", color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = onAddRoom, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Add Room", tint = CyanBlue)
                        }
                    }

                    rooms.forEach { room ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(room.name, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Button(
                                onClick = { onManageSeats(room.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue.copy(alpha = 0.1f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("SƠ ĐỒ GHẾ", color = CyanBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onDeleteRoom(room.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Xóa phòng",
                                    tint = Color.Red.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminCinemaManagementPreview() {
    CAH_CinemaTheme {
        AdminCinemaManagementContent(
            state = AdminCinemaState(
                cinemas = listOf(
                    CinemaItem(1, "Cinestar Quốc Thanh", "271 Nguyễn Trãi, Q.1, TP.HCM", "028 7300 8881", null),
                    CinemaItem(2, "Cinestar Hai Bà Trưng", "233 Hai Bà Trưng, Q.3, TP.HCM", "028 7300 7279", null)
                ),
                isLoading = false
            ),
            onNavigate = {},
            onDeleteCinema = {},
            onAddClick = {},
            onAddRoomClick = {},
            onDeleteRoom = { _, _ -> },
            onManageSeats = {}
        )
    }
}
