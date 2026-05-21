package com.example.cah_cinema.presentation.admin.cinema

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.LocationOn
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
    
    var showDialogByCinema by remember { mutableStateOf<CinemaItem?>(null) }
    var isAddingCinema by remember { mutableStateOf(false) }
    
    var showDialogByRoom by remember { mutableStateOf<RoomItem?>(null) }
    var isAddingRoomToCinemaId by remember { mutableStateOf<Long?>(null) }

    AdminCinemaManagementContent(
        state = state,
        onNavigate = onNavigate,
        onDeleteCinema = { viewModel.deleteCinema(it) },
        onAddClick = { isAddingCinema = true },
        onEditClick = { showDialogByCinema = it },
        onAddRoomClick = { isAddingRoomToCinemaId = it },
        onEditRoomClick = { showDialogByRoom = it },
        onDeleteRoomClick = { room -> viewModel.deleteRoom(room.id, room.cinemaId) },
        onManageSeats = { roomId -> onNavigate(Screen.AdminSeatManagement.createRoute(roomId)) }
    )

    if (isAddingCinema || showDialogByCinema != null) {
        AddCinemaDialog(
            cinema = showDialogByCinema,
            rooms = showDialogByCinema?.id?.let { state.roomsByCinema[it] } ?: emptyList(),
            onDismiss = { 
                isAddingCinema = false
                showDialogByCinema = null
            },
            onConfirm = { name, address, hotline ->
                if (isAddingCinema) {
                    viewModel.createCinema(name, address, hotline) { isAddingCinema = false }
                } else {
                    showDialogByCinema?.id?.let { id ->
                        viewModel.updateCinema(id, name, address, hotline) { showDialogByCinema = null }
                    }
                }
            },
            onAddRoom = { showDialogByCinema?.id?.let { isAddingRoomToCinemaId = it } },
            onEditRoom = { showDialogByRoom = it },
            onDeleteRoom = { room -> viewModel.deleteRoom(room.id, room.cinemaId) },
            onManageSeats = { roomId -> onNavigate(Screen.AdminSeatManagement.createRoute(roomId)) }
        )
    }

    if (isAddingRoomToCinemaId != null || showDialogByRoom != null) {
        AddRoomDialog(
            room = showDialogByRoom,
            onDismiss = { 
                isAddingRoomToCinemaId = null
                showDialogByRoom = null
            },
            onConfirm = { roomName ->
                if (isAddingRoomToCinemaId != null) {
                    viewModel.createRoom(isAddingRoomToCinemaId!!, roomName) { isAddingRoomToCinemaId = null }
                } else {
                    showDialogByRoom?.let { room ->
                        viewModel.updateRoom(room.id, room.cinemaId, roomName) { showDialogByRoom = null }
                    }
                }
            }
        )
    }
}

@Composable
fun AdminCinemaManagementContent(
    state: AdminCinemaState,
    onNavigate: (String) -> Unit,
    onDeleteCinema: (Long) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (CinemaItem) -> Unit,
    onAddRoomClick: (Long) -> Unit,
    onEditRoomClick: (RoomItem) -> Unit,
    onDeleteRoomClick: (RoomItem) -> Unit,
    onManageSeats: (Long) -> Unit
) {
    AdminScaffold(
        title = "Quản lý Rạp"
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
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("THÊM RẠP MỚI", color = Color.Black, fontWeight = FontWeight.Bold)
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
                            onEdit = { onEditClick(cinema) },
                            onAddRoom = { onAddRoomClick(cinema.id) },
                            onEditRoom = onEditRoomClick,
                            onDeleteRoom = onDeleteRoomClick,
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
    room: RoomItem? = null,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var roomName by remember { mutableStateOf(room?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (room == null) "Thêm phòng chiếu mới" else "Cập nhật phòng chiếu", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminTextField(
                    value = roomName, 
                    onValueChange = { roomName = it }, 
                    label = "Tên phòng (ví dụ: Phòng 01)"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(roomName) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = roomName.isNotBlank()
            ) {
                Text(if (room == null) "XÁC NHẬN" else "CẬP NHẬT", color = Color.Black, fontWeight = FontWeight.Bold)
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
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value, 
        onValueChange = onValueChange, 
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
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
fun AddCinemaDialog(
    cinema: CinemaItem? = null,
    rooms: List<RoomItem> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit,
    onAddRoom: () -> Unit = {},
    onEditRoom: (RoomItem) -> Unit = {},
    onDeleteRoom: (RoomItem) -> Unit = {},
    onManageSeats: (Long) -> Unit = {}
) {
    var name by remember { mutableStateOf(cinema?.name ?: "") }
    var address by remember { mutableStateOf(cinema?.address ?: "") }
    var hotline by remember { mutableStateOf(cinema?.hotline ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (cinema == null) "Thêm rạp mới" else "Cập nhật rạp chiếu", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AdminTextField(value = name, onValueChange = { name = it }, label = "Tên rạp")
                AdminTextField(value = address, onValueChange = { address = it }, label = "Địa chỉ")
                AdminTextField(value = hotline, onValueChange = { hotline = it }, label = "Hotline")

                if (cinema != null) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("QUẢN LÝ PHÒNG CHIẾU", color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                            Text(room.name, color = Color.White, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { onEditRoom(room) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { onDeleteRoom(room) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                                }
                                TextButton(
                                    onClick = { onManageSeats(room.id) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(24.dp)
                                ) {
                                    Text("SƠ ĐỒ", color = CyanBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, address, hotline) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = name.isNotBlank() && address.isNotBlank()
            ) {
                Text(if (cinema == null) "XÁC NHẬN" else "LƯU THÔNG TIN", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY", color = Color.White.copy(alpha = 0.6f)) }
        },
        containerColor = Color(0xFF21212B)
    )
}

@Composable
fun CinemaCard(
    cinema: CinemaItem, 
    rooms: List<RoomItem>,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onAddRoom: () -> Unit,
    onEditRoom: (RoomItem) -> Unit,
    onDeleteRoom: (RoomItem) -> Unit,
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
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(26.dp))
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(cinema.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(cinema.address, color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyMedium)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onEdit) {
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
                            Text(room.name, color = Color.White, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(onClick = { onEditRoom(room) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Room", tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { onDeleteRoom(room) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Room", tint = Color.Red.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                                }
                                Button(
                                    onClick = { onManageSeats(room.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue.copy(alpha = 0.1f)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("SƠ ĐỒ GHẾ", color = CyanBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
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
                    CinemaItem(1, "Cinestar Quốc Thanh", "271 Nguyễn Trãi, Q.1, TP.HCM", null, "028 7300 8881"),
                    CinemaItem(2, "Cinestar Hai Bà Trưng", "233 Hai Bà Trưng, Q.3, TP.HCM", null, "028 7300 7279")
                ),
                isLoading = false
            ),
            onNavigate = {},
            onDeleteCinema = {},
            onAddClick = {},
            onEditClick = {},
            onAddRoomClick = {},
            onEditRoomClick = {},
            onDeleteRoomClick = {},
            onManageSeats = {}
        )
    }
}
