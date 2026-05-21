package com.example.cah_cinema.presentation.admin.food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.R
import com.example.cah_cinema.data.model.FoodItem
import com.example.cah_cinema.presentation.admin.cinema.AdminTextField
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.ui.theme.TextGray
import com.example.cah_cinema.presentation.user.booking.formatPrice

@Composable
fun AdminFoodScreen(
    viewModel: AdminFoodViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf<FoodItem?>(null) }
    var isAdding by remember { mutableStateOf(false) }

    AdminScaffold(title = "Quản lý Đồ ăn") { paddingValues ->
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
                    text = "Danh sách sản phẩm (${state.foods.size})",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { isAdding = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("THÊM MÓN MỚI", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading && state.foods.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanBlue)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.foods) { food ->
                        FoodAdminCard(
                            food = food,
                            onEdit = { showDialog = food },
                            onDelete = { viewModel.deleteFood(food.id) }
                        )
                    }
                }
            }
        }
    }

    if (isAdding || showDialog != null) {
        AddEditFoodDialog(
            food = showDialog,
            onDismiss = {
                isAdding = false
                showDialog = null
            },
            onConfirm = { updatedFood ->
                if (isAdding) {
                    viewModel.createFood(updatedFood) { isAdding = false }
                } else {
                    showDialog?.id?.let { id ->
                        viewModel.updateFood(id, updatedFood) { showDialog = null }
                    }
                }
            }
        )
    }
}

@Composable
fun FoodAdminCard(
    food: FoodItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = Color(0xFF1C1C22),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column {
            Box(modifier = Modifier.height(140.dp).fillMaxWidth()) {
                AsyncImage(
                    model = food.imageUrl,
                    contentDescription = food.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.pepsi)
                )
                Surface(
                    modifier = Modifier.padding(8.dp).align(Alignment.TopEnd),
                    color = if (food.available) CyanBlue else Color.Gray,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (food.available) "CÒN HÀNG" else "HẾT HÀNG",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = food.category,
                    color = CyanBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = food.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = formatPrice(food.price),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SỬA", color = Color.White, fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("XÓA", color = Color.Red, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditFoodDialog(
    food: FoodItem? = null,
    onDismiss: () -> Unit,
    onConfirm: (FoodItem) -> Unit
) {
    var name by remember { mutableStateOf(food?.name ?: "") }
    var description by remember { mutableStateOf(food?.description ?: "") }
    var price by remember { mutableStateOf(food?.price?.toString() ?: "") }
    var category by remember { mutableStateOf(food?.category ?: "COMBO") }
    var imageUrl by remember { mutableStateOf(food?.imageUrl ?: "") }
    var available by remember { mutableStateOf(food?.available ?: true) }

    val categories = listOf("CORN", "DRINK", "COMBO", "SNACK")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (food == null) "Thêm sản phẩm mới" else "Cập nhật sản phẩm", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AdminTextField(value = name, onValueChange = { name = it }, label = "Tên món")
                AdminTextField(value = description, onValueChange = { description = it }, label = "Mô tả")
                AdminTextField(value = price, onValueChange = { price = it }, label = "Giá bán (VNĐ)")
                AdminTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = "Link ảnh URL")
                
                Column {
                    Text("Loại sản phẩm", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanBlue,
                                    selectedLabelColor = Color.Black,
                                    labelColor = Color.White.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = available,
                        onCheckedChange = { available = it },
                        colors = CheckboxDefaults.colors(checkedColor = CyanBlue)
                    )
                    Text("Còn hàng (Available)", color = Color.White)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        FoodItem(
                            id = food?.id ?: 0L,
                            name = name,
                            description = description,
                            price = price.toDoubleOrNull() ?: 0.0,
                            category = category,
                            imageUrl = imageUrl,
                            available = available
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = name.isNotBlank() && price.isNotBlank()
            ) {
                Text(if (food == null) "XÁC NHẬN" else "CẬP NHẬT", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY", color = Color.White.copy(alpha = 0.6f)) }
        },
        containerColor = Color(0xFF21212B)
    )
}
