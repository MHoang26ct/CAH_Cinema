package com.example.cah_cinema.presentation.admin.promotion

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.data.model.AdminPromotionDetail
import com.example.cah_cinema.data.model.AdminPromotionItem
import com.example.cah_cinema.data.model.CreateOrUpdatePromotionRequest
import com.example.cah_cinema.presentation.admin.components.AdminScaffold
import com.example.cah_cinema.presentation.admin.components.AdminTextField
import com.example.cah_cinema.presentation.admin.promotion.AdminPromotionState
import com.example.cah_cinema.ui.theme.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun AdminPromotionManagementScreen(
    viewModel: AdminPromotionViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    AdminPromotionManagementContent(
        state = state,
        onAddClick = { viewModel.loadPromotions() /* or other action if needed */ },
        onEdit = { viewModel.loadPromotionDetail(it.id) },
        onDelete = { viewModel.deletePromotion(it) },
        onUploadImage = { context, uri, callback -> viewModel.uploadImage(context, uri, callback) },
        onConfirmAdd = { request -> viewModel.createPromotion(request) {} },
        onConfirmUpdate = { id, request -> viewModel.updatePromotion(id, request) {} },
        onClearDetail = { viewModel.clearEditingDetail() },
        onClearMessages = { viewModel.clearMessages() }
    )
}

@Composable
fun AdminPromotionManagementContent(
    state: AdminPromotionState,
    onAddClick: () -> Unit,
    onEdit: (AdminPromotionItem) -> Unit,
    onDelete: (Long) -> Unit,
    onUploadImage: (android.content.Context, Uri, (String?) -> Unit) -> Unit,
    onConfirmAdd: (CreateOrUpdatePromotionRequest) -> Unit,
    onConfirmUpdate: (Long, CreateOrUpdatePromotionRequest) -> Unit,
    onClearDetail: () -> Unit,
    onClearMessages: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingPromotion by remember { mutableStateOf<AdminPromotionItem?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }

    AdminScaffold(title = "Quản lý Khuyến mãi", snackbarHostState = snackbarHostState) { paddingValues ->
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
                    text = "Danh sách khuyến mãi (${state.promotions.size})",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("THÊM MỚI", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading && state.promotions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanBlue)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C22), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TIÊU ĐỀ", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(3f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("THỜI GIAN", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("TRẠNG THÁI", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("THAO TÁC", color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .background(Color(0xFF1C1C22).copy(alpha = 0.5f))
                ) {
                    items(state.promotions) { promotion ->
                        PromotionRow(
                            promotion = promotion,
                            onEdit = { 
                                onEdit(promotion)
                                editingPromotion = promotion 
                            },
                            onDelete = { onDelete(promotion.id) }
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        PromotionFormDialog(
            title = "Thêm khuyến mãi mới",
            isUploading = state.isUploading,
            onDismiss = { showAddDialog = false },
            onUploadImage = onUploadImage,
            onConfirm = { request -> 
                onConfirmAdd(request)
                showAddDialog = false 
            }
        )
    }

    editingPromotion?.let { promotion ->
        if (state.isLoadingDetail) {
            AlertDialog(
                onDismissRequest = { editingPromotion = null },
                confirmButton = {},
                text = { Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CyanBlue) } },
                containerColor = Color(0xFF21212B)
            )
        } else {
            PromotionFormDialog(
                title = "Sửa khuyến mãi",
                isUploading = state.isUploading,
                initialDetail = state.currentPromotionDetail,
                onDismiss = { 
                    editingPromotion = null
                    onClearDetail()
                },
                onUploadImage = onUploadImage,
                onConfirm = { request ->
                    onConfirmUpdate(promotion.id, request)
                    editingPromotion = null
                    onClearDetail()
                }
            )
        }
    }
}

@Composable
fun PromotionRow(
    promotion: AdminPromotionItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(3f), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = promotion.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp, 40.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = promotion.title,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Column(modifier = Modifier.weight(2f)) {
            Text(text = "Từ: ${promotion.startAt ?: "N/A"}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            Text(text = "Đến: ${promotion.expiredAt ?: "N/A"}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        }

        Surface(
            modifier = Modifier.weight(1.5f),
            color = if (promotion.isActive) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = if (promotion.isActive) "ĐANG CHẠY" else "TẠM DỪNG",
                color = if (promotion.isActive) Color(0xFF4CAF50) else Color.Red,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Row(modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = CyanBlue, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionFormDialog(
    title: String,
    isUploading: Boolean,
    initialDetail: AdminPromotionDetail? = null,
    onDismiss: () -> Unit,
    onUploadImage: (android.content.Context, Uri, (String?) -> Unit) -> Unit,
    onConfirm: (CreateOrUpdatePromotionRequest) -> Unit
) {
    val context = LocalContext.current
    val gson = Gson()

    var promoTitle by remember { mutableStateOf(initialDetail?.title ?: "") }
    var description by remember { mutableStateOf(initialDetail?.description ?: "") }
    var imageUrl by remember { mutableStateOf(initialDetail?.imageUrl ?: "") }
    
    // Parse dates
    val initialStart = initialDetail?.startAt ?: "2026-01-01"
    val startParts = initialStart.split("-")
    var startDay by remember { mutableStateOf(if (startParts.size == 3) startParts[2] else "01") }
    var startMonth by remember { mutableStateOf(if (startParts.size == 3) startParts[1] else "01") }
    var startYear by remember { mutableStateOf(if (startParts.size == 3) startParts[0] else "2026") }

    val initialEnd = initialDetail?.expiredAt ?: "2026-12-31"
    val endParts = initialEnd.split("-")
    var endDay by remember { mutableStateOf(if (endParts.size == 3) endParts[2] else "31") }
    var endMonth by remember { mutableStateOf(if (endParts.size == 3) endParts[1] else "12") }
    var endYear by remember { mutableStateOf(if (endParts.size == 3) endParts[0] else "2026") }

    var isActive by remember { mutableStateOf(initialDetail?.isActive ?: true) }

    // Multi-line list inputs
    val conditions = remember { 
        mutableStateListOf<String>().apply {
            initialDetail?.conditions?.let {
                try {
                    val list: List<String> = gson.fromJson(it, object : TypeToken<List<String>>() {}.type)
                    addAll(list)
                } catch (_: Exception) {}
            }
        }
    }
    val notes = remember { 
        mutableStateListOf<String>().apply {
            initialDetail?.notes?.let {
                try {
                    val list: List<String> = gson.fromJson(it, object : TypeToken<List<String>>() {}.type)
                    addAll(list)
                } catch (_: Exception) {}
            }
        }
    }

    var newCondition by remember { mutableStateOf("") }
    var newNote by remember { mutableStateOf("") }

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
                AdminTextField(value = promoTitle, onValueChange = { promoTitle = it }, label = "Tiêu đề")
                AdminTextField(value = description, onValueChange = { description = it }, label = "Mô tả ngắn", singleLine = false)

                Text("Ngày bắt đầu", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(value = startDay, onValueChange = { if (it.length <= 2) startDay = it }, label = "Ngày", modifier = Modifier.weight(1f))
                    AdminTextField(value = startMonth, onValueChange = { if (it.length <= 2) startMonth = it }, label = "Tháng", modifier = Modifier.weight(1f))
                    AdminTextField(value = startYear, onValueChange = { if (it.length <= 4) startYear = it }, label = "Năm", modifier = Modifier.weight(1.5f))
                }

                Text("Ngày kết thúc", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(value = endDay, onValueChange = { if (it.length <= 2) endDay = it }, label = "Ngày", modifier = Modifier.weight(1f))
                    AdminTextField(value = endMonth, onValueChange = { if (it.length <= 2) endMonth = it }, label = "Tháng", modifier = Modifier.weight(1f))
                    AdminTextField(value = endYear, onValueChange = { if (it.length <= 4) endYear = it }, label = "Năm", modifier = Modifier.weight(1.5f))
                }

                Text("Hình ảnh banner", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = "URL Hình ảnh", modifier = Modifier.weight(1f))
                    IconButton(onClick = { imageLauncher.launch("image/*") }, enabled = !isUploading) {
                        if (isUploading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CyanBlue, strokeWidth = 2.dp)
                        else Icon(Icons.Default.Image, contentDescription = "Chọn ảnh", tint = CyanBlue)
                    }
                }
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(model = imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                }

                // Conditions management
                Text("Điều kiện áp dụng", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                conditions.forEachIndexed { index, cond ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("• $cond", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { conditions.removeAt(index) }) { Icon(Icons.Default.Close, null, tint = Color.Red, modifier = Modifier.size(16.dp)) }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AdminTextField(value = newCondition, onValueChange = { newCondition = it }, label = "Thêm điều kiện...", modifier = Modifier.weight(1f))
                    IconButton(onClick = { if (newCondition.isNotBlank()) { conditions.add(newCondition); newCondition = "" } }) { Icon(Icons.Default.Add, null, tint = CyanBlue) }
                }

                // Notes management
                Text("Lưu ý", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                notes.forEachIndexed { index, note ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("• $note", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { notes.removeAt(index) }) { Icon(Icons.Default.Close, null, tint = Color.Red, modifier = Modifier.size(16.dp)) }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AdminTextField(value = newNote, onValueChange = { newNote = it }, label = "Thêm lưu ý...", modifier = Modifier.weight(1f))
                    IconButton(onClick = { if (newNote.isNotBlank()) { notes.add(newNote); newNote = "" } }) { Icon(Icons.Default.Add, null, tint = CyanBlue) }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isActive, onCheckedChange = { isActive = it }, colors = CheckboxDefaults.colors(checkedColor = CyanBlue))
                    Text("Đang kích hoạt", color = Color.White, fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startAt = "$startYear-${startMonth.padStart(2, '0')}-${startDay.padStart(2, '0')}"
                    val expiredAt = "$endYear-${endMonth.padStart(2, '0')}-${endDay.padStart(2, '0')}"
                    onConfirm(CreateOrUpdatePromotionRequest(
                        title = promoTitle,
                        description = description,
                        imageUrl = imageUrl,
                        conditions = conditions.toList(),
                        notes = notes.toList(),
                        startAt = startAt,
                        expiredAt = expiredAt,
                        isActive = isActive
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                enabled = promoTitle.isNotBlank() && !isUploading
            ) {
                Text("XÁC NHẬN", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("HỦY", color = Color.White.copy(alpha = 0.6f)) } },
        containerColor = Color(0xFF21212B)
    )
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape")
@Composable
fun AdminPromotionManagementPreview() {
    MaterialTheme {
        AdminPromotionManagementContent(
            state = AdminPromotionState(
                promotions = listOf(
                    AdminPromotionItem(1L, "ƯU ĐÃI HỌC SINH SINH VIÊN", "Giá vé chỉ từ 45k cho HSSV", "https://via.placeholder.com/300x160", "2026-01-01", "2026-12-31", true),
                    AdminPromotionItem(2L, "GIẢM 50% COMBO BẮP NƯỚC", "Áp dụng khi mua kèm 2 vé", "https://via.placeholder.com/300x160", "2026-05-01", "2026-05-31", false)
                ),
                isLoading = false
            ),
            onAddClick = {},
            onEdit = { _ -> },
            onDelete = { _ -> },
            onUploadImage = { _, _, _ -> },
            onConfirmAdd = { _ -> },
            onConfirmUpdate = { _, _ -> },
            onClearDetail = {},
            onClearMessages = {}
        )
    }
}
