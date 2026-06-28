package com.example.cah_cinema.presentation.staff.checkin

import android.Manifest
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.data.model.CheckInResponse
import com.example.cah_cinema.ui.theme.CyanBlue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StaffCheckInScreen(
    viewModel: StaffCheckInViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var showManualInputDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (cameraPermission.status.isGranted) {
            viewModel.startScanning()
        }
    }

    LaunchedEffect(cameraPermission.status.isGranted) {
        if (cameraPermission.status.isGranted && uiState is CheckInUiState.Idle) {
            viewModel.startScanning()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13131A))
    ) {
        when (val state = uiState) {
            is CheckInUiState.Idle -> {
                // Hỏi quyền camera
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = CyanBlue,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Cần quyền truy cập Camera",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Ứng dụng cần quyền camera để quét mã QR",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { cameraPermission.launchPermissionRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cấp quyền camera", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            is CheckInUiState.Scanning -> {
                CameraQrScanner(
                    onQrScanned = { viewModel.onQrCodeScanned(it) }
                )
                // Overlay UI
                QrScannerOverlay(
                    onBackClick = onBackClick,
                    onManualInputClick = { showManualInputDialog = true }
                )
            }

            is CheckInUiState.Loading -> {
                // Giữ camera ẩn bên dưới, hiện loading overlay
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = CyanBlue, strokeWidth = 3.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Đang xác nhận vé...", color = Color.White.copy(alpha = 0.7f))
                }
            }

            is CheckInUiState.Success -> {
                CheckInSuccessView(
                    result = state.result,
                    onScanNext = { viewModel.resetToScanning() },
                    onBack = {
                        viewModel.reset()
                        onBackClick()
                    }
                )
            }

            is CheckInUiState.Error -> {
                CheckInErrorView(
                    message = state.message,
                    onRetry = { viewModel.resetToScanning() },
                    onBack = {
                        viewModel.reset()
                        onBackClick()
                    }
                )
            }
        }

        // Back button (chỉ ở scanning state)
        if (uiState is CheckInUiState.Scanning) {
            // Overlay back button – already in QrScannerOverlay
        }
    }

    if (showManualInputDialog) {
        ManualTokenInputDialog(
            onDismiss = { showManualInputDialog = false },
            onConfirm = { token ->
                showManualInputDialog = false
                viewModel.checkIn(token)
            }
        )
    }
}

@Composable
private fun ManualTokenInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var token by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C22),
        title = {
            Text("Nhập mã vé thủ công", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    "Nhập chuỗi token từ mã QR hoặc mã vé hiển thị trên app khách.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    placeholder = { Text("Mã vé...", color = Color.White.copy(alpha = 0.3f)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = CyanBlue,
                        focusedBorderColor = CyanBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (token.isNotBlank()) onConfirm(token) },
                enabled = token.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Xác nhận", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = Color.White.copy(alpha = 0.6f))
            }
        }
    )
}

@Composable
private fun CameraQrScanner(
    onQrScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var lastScannedTime by remember { mutableLongStateOf(0L) }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720)) // Sử dụng độ phân giải chuẩn 16:9
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            val result = scanQrFromImage(imageProxy)
                            if (result != null) {
                                val now = System.currentTimeMillis()
                                if (now - lastScannedTime > 2000) { // debounce 2s
                                    lastScannedTime = now
                                    onQrScanned(result)
                                }
                            }
                            imageProxy.close()
                        }
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Suppress("UnsafeOptInUsageError")
private fun scanQrFromImage(imageProxy: ImageProxy): String? {
    return try {
        val plane = imageProxy.planes[0]
        val buffer = plane.buffer
        val width = imageProxy.width
        val height = imageProxy.height
        val rowStride = plane.rowStride
        
        val yuvData = ByteArray(buffer.remaining())
        buffer.get(yuvData)

        // Phải dùng rowStride làm width thực tế của buffer để PlanarYUVLuminanceSource đọc đúng
        val source = PlanarYUVLuminanceSource(
            yuvData, rowStride, height,
            0, 0, width, height, false
        )
        
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        
        val hints = mutableMapOf<DecodeHintType, Any>()
        hints[DecodeHintType.POSSIBLE_FORMATS] = listOf(BarcodeFormat.QR_CODE)
        hints[DecodeHintType.TRY_HARDER] = true // Chế độ quét kỹ hơn cho JWT dài
        hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
        
        val reader = MultiFormatReader()
        try {
            return reader.decode(binaryBitmap, hints).text
        } catch (e: Exception) {
            // Thử xoay ảnh nếu quét thẳng thất bại
            try {
                val rotatedSource = source.rotateCounterClockwise()
                val rotatedBitmap = BinaryBitmap(HybridBinarizer(rotatedSource))
                return reader.decode(rotatedBitmap, hints).text
            } catch (e2: Exception) {
                null
            }
        }
    } catch (e: Exception) {
        null
    }
}

@Composable
private fun QrScannerOverlay(onBackClick: () -> Unit, onManualInputClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Quét mã QR vé",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onManualInputClick) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Nhập tay",
                    tint = Color.White
                )
            }
        }

        // Center scanning frame
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Dark overlay top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.25f)
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
            )
            // Dark overlay bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.25f)
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            // Scan frame
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .border(2.dp, CyanBlue, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                // Scan line animation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(CyanBlue.copy(alpha = 0.8f))
                        .offset(y = (260 * scanLineOffset).dp)
                )
            }
        }

        // Bottom instruction
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.QrCode,
                contentDescription = null,
                tint = CyanBlue,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Đưa mã QR vào khung để quét",
                color = Color.White,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Hệ thống sẽ tự động nhận diện",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun CheckInSuccessView(
    result: CheckInResponse,
    onScanNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13131A))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success icon
        Surface(
            color = Color(0xFF4CAF50).copy(alpha = 0.15f),
            shape = CircleShape,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Check-in thành công!",
            color = Color(0xFF4CAF50),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Ticket info card
        Surface(
            color = Color(0xFF1C1C22),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CheckInInfoRow(
                    icon = Icons.Filled.Movie,
                    label = "Phim",
                    value = result.movieTitle
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                CheckInInfoRow(
                    icon = Icons.Filled.LocationOn,
                    label = "Rạp",
                    value = result.cinemaName
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                CheckInInfoRow(
                    icon = Icons.Filled.MeetingRoom,
                    label = "Phòng",
                    value = result.roomName
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                CheckInInfoRow(
                    icon = Icons.Filled.AccessTime,
                    label = "Suất chiếu",
                    value = result.startTime
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                CheckInInfoRow(
                    icon = Icons.Filled.Chair,
                    label = "Ghế",
                    value = result.seatName
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onScanNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Quét vé tiếp theo", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBack) {
            Text("Về Dashboard", color = Color.White.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun CheckInErrorView(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF13131A))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = Color.Red.copy(alpha = 0.12f),
            shape = CircleShape,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Check-in thất bại",
            color = Color.Red,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            message,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Thử lại", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBack) {
            Text("Về Dashboard", color = Color.White.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun CheckInInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = CyanBlue.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
            Text(
                value,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
