package com.example.cah_cinema.presentation.admin.cinema

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.data.model.CinemaItem
import com.example.cah_cinema.data.model.RoomItem
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.admin.components.AdminTextField
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
    var editingCinema by remember { mutableStateOf<CinemaItem?>(null) }
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
        onEditClick = { editingCinema = it },
        onAddRoomClick = { showAddRoomDialog = it },
        onDeleteRoom = { roomId, cinemaId -> viewModel.deleteRoom(roomId, cinemaId) },
        onManageSeats = { roomId -> onNavigate(Screen.AdminSeatManagement.createRoute(roomId)) }
    )

    if (showAddDialog) {
        CinemaFormDialog(
            title = "Thêm rạp mới",
            isUploading = state.isUploading,
            onDismiss = { showAddDialog = false },
            onUploadImage = { context, uri, callback -> viewModel.uploadImage(context, uri, callback) },
            onConfirm = { name, address, hotline, imageUrl ->
                viewModel.createCinema(name, address, hotline, imageUrl) {
                    showAddDialog = false
                }
            }
        )
    }

    editingCinema?.let { cinema ->
        CinemaFormDialog(
            title = "Sửa thông tin rạp",
            initialData = cinema,
            isUploading = state.isUploading,
            onDismiss = { editingCinema = null },
            onUploadImage = { context, uri, callback -> viewModel.uploadImage(context, uri, callback) },
            onConfirm = { name, address, hotline, imageUrl ->
                viewModel.updateCinema(cinema.id, name, address, hotline, imageUrl) {
                    editingCinema = null
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
    onEditClick: (CinemaItem) -> Unit,
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.cinemas) { cinema ->
                        CinemaCard(
                            cinema = cinema, 
                            rooms = state.roomsByCinema[cinema.id] ?: emptyList(),
                            onEdit = { onEditClick(cinema) },
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
fun CinemaFormDialog(
    title: String,
    initialData: CinemaItem? = null,
    isUploading: Boolean,
    onDismiss: () -> Unit,
    onUploadImage: (android.content.Context, Uri, (String?) -> Unit) -> Unit,
    onConfirm: (String, String, String, String?) -> Unit
) {
    var name by remember { mutableStateOf(initialData?.name ?: "") }
    var address by remember { mutableStateOf(initialData?.address ?: "") }
    var hotline by remember { mutableStateOf(initialData?.hotline ?: "") }
    var imageUrl by remember { mutableStateOf(initialData?.imageUrl ?: "") }
    
    val context = LocalContext.current
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onUploadImage(context, it) { url -> if (url != null) imageUrl = url } }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = "Tên rạp"
                )
                AdminTextField(
                    value = address, 
                    onValueChange = { address = it }, 
                    label = "Địa chỉ"
                )
                AdminTextField(
                    value = hotline, 
                    onValueChange = { hotline = it }, 
                    label = "Hotline"
                )
                
                Text("Hình ảnh rạp", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = "URL Hình ảnh",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { imageLauncher.launch("image/*") }, enabled = !isUploading) {
                        if (isUploading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CyanBlue, strokeWidth = 2.dp)
                        else Icon(Icons.Default.Image, contentDescription = "Chọn ảnh", tint = CyanBlue)
                    }
                }
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl, 
                        contentDescription = null, 
                        modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)), 
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, address, hotline, imageUrl.ifBlank { null }) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = name.isNotBlank() && address.isNotBlank() && !isUploading
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
fun CinemaCard(
    cinema: CinemaItem, 
    rooms: List<RoomItem>,
    onEdit: () -> Unit,
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
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CyanBlue.copy(alpha = 0.7f))
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

@OptIn(ExperimentalMaterial3Api::class)
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
            onEditClick = {},
            onAddRoomClick = {},
            onDeleteRoom = { _, _ -> },
            onManageSeats = {}
        )
    }
}
