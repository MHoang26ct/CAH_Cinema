package com.example.cah_cinema.presentation.admin.food

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.data.model.FoodItem
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.admin.components.AdminTextField
import com.example.cah_cinema.presentation.user.booking.formatPrice
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme
import com.example.cah_cinema.ui.theme.CyanBlue

@Composable
fun AdminFoodManagementScreen(
    viewModel: AdminFoodViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingFood by remember { mutableStateOf<FoodItem?>(null) }

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

    AdminFoodManagementContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAddClick = { showAddDialog = true },
        onEditFood = { food -> editingFood = food },
        onDeleteFood = { viewModel.deleteFood(it) }
    )

    if (showAddDialog) {
        FoodFormDialog(
            title = "Thêm món mới",
            isUploading = state.isUploading,
            onDismiss = { showAddDialog = false },
            onUploadImage = { context, uri, callback -> viewModel.uploadFoodImage(context, uri, callback) },
            onConfirm = { food -> viewModel.createFood(food) { showAddDialog = false } }
        )
    }

    editingFood?.let { food ->
        FoodFormDialog(
            title = "Sửa thông tin món",
            initialData = food,
            isUploading = state.isUploading,
            onDismiss = { editingFood = null },
            onUploadImage = { context, uri, callback -> viewModel.uploadFoodImage(context, uri, callback) },
            onConfirm = { updatedFood -> viewModel.updateFood(food.id, updatedFood) { editingFood = null } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodFormDialog(
    title: String,
    initialData: FoodItem? = null,
    isUploading: Boolean,
    onDismiss: () -> Unit,
    onUploadImage: (android.content.Context, Uri, (String?) -> Unit) -> Unit,
    onConfirm: (FoodItem) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(initialData?.name ?: "") }
    var description by remember { mutableStateOf(initialData?.description ?: "") }
    var price by remember { mutableStateOf(initialData?.price?.toInt()?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(initialData?.imageUrl ?: "") }
    var category by remember { mutableStateOf(initialData?.category ?: "Food") }
    
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Food", "Drink", "Combo")

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
                AdminTextField(value = name, onValueChange = { name = it }, label = "Tên món")
                AdminTextField(value = description, onValueChange = { description = it }, label = "Mô tả", singleLine = false)
                AdminTextField(value = price, onValueChange = { price = it }, label = "Giá tiền (VNĐ)")
                
                Text("Phân loại", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    AdminTextField(
                        value = category,
                        onValueChange = {},
                        label = "",
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(Color(0xFF2D2D35))
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = Color.White) },
                                onClick = { category = cat; categoryExpanded = false }
                            )
                        }
                    }
                }

                Text("Hình ảnh", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(
                        value = imageUrl, onValueChange = { imageUrl = it }, label = "URL Ảnh",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { imageLauncher.launch("image/*") }, enabled = !isUploading) {
                        if (isUploading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CyanBlue, strokeWidth = 2.dp)
                        else Icon(Icons.Default.Image, contentDescription = null, tint = CyanBlue)
                    }
                }
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl, contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(FoodItem(initialData?.id ?: 0, name, description, price.toDoubleOrNull() ?: 0.0, category, imageUrl))
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = name.isNotBlank() && price.isNotBlank() && !isUploading
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
fun FoodRow(food: FoodItem, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(3f), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = food.imageUrl, contentDescription = null,
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(food.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(food.description, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Text(food.category, color = CyanBlue, modifier = Modifier.weight(1.5f), fontSize = 13.sp)
        Text(formatPrice(food.price), color = Color.White, modifier = Modifier.weight(1.5f), fontSize = 13.sp)
        Row(modifier = Modifier.weight(1.5f)) {
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = null, tint = CyanBlue.copy(alpha = 0.8f), modifier = Modifier.size(20.dp)) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(20.dp)) }
        }
    }
}

@Composable
fun AdminFoodManagementContent(
    state: AdminFoodState,
    onAddClick: () -> Unit,
    onEditFood: (FoodItem) -> Unit,
    onDeleteFood: (Long) -> Unit,
    snackbarHostState: SnackbarHostState = SnackbarHostState()
) {
    AdminScaffold(title = "Quản lý Đồ ăn & Thức uống", snackbarHostState = snackbarHostState) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Danh sách (${state.foods.size})",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("THÊM MÓN", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading && state.foods.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanBlue)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C22), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text("MÓN ĂN / NƯỚC UỐNG", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(3f), style = MaterialTheme.typography.labelMedium)
                    Text("PHÂN LOẠI", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium)
                    Text("GIÁ TIỀN", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium)
                    Text("THAO TÁC", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .background(Color(0xFF1C1C22).copy(alpha = 0.5f))
                ) {
                    items(state.foods) { food ->
                        FoodRow(
                            food = food,
                            onEdit = { onEditFood(food) },
                            onDelete = { onDeleteFood(food.id) }
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminFoodManagementPreview() {
    CAH_CinemaTheme {
        AdminFoodManagementContent(
            state = AdminFoodState(
                foods = listOf(
                    FoodItem(1, "Bắp rang bơ lớn", "Bắp rang bơ size L", 45000.0, "Food", ""),
                    FoodItem(2, "Coca Cola", "Nước ngọt có ga 500ml", 30000.0, "Drink", ""),
                    FoodItem(3, "Combo đôi", "2 bắp + 2 nước", 120000.0, "Combo", "")
                ),
                isLoading = false
            ),
            onAddClick = {},
            onEditFood = {},
            onDeleteFood = {}
        )
    }
}
