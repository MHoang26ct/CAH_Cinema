package com.example.cah_cinema.presentation.admin.seats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.ui.theme.CyanBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSeatManagementScreen(
    viewModel: AdminSeatManagementViewModel = viewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedTypeId by remember { mutableStateOf(1L) } // 1: Regular, 2: VIP, 3: Couple

    Scaffold(
        containerColor = Color(0xFF13131A),
        topBar = {
            TopAppBar(
                title = { Text("THIẾT KẾ SƠ ĐỒ GHẾ", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.saveSeatMap(onBack) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("LƯU SƠ ĐỒ", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF13131A))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            // Legend / Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeatTypeSelector("THƯỜNG", Color.Gray, selectedTypeId == 1L) { selectedTypeId = 1L }
                SeatTypeSelector("VIP", Color(0xFFFFD700), selectedTypeId == 2L) { selectedTypeId = 2L }
                SeatTypeSelector("COUPLE", Color(0xFFFF69B4), selectedTypeId == 3L) { selectedTypeId = 3L }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Screen Indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            )
            Text(
                "MÀN HÌNH", 
                color = Color.White.copy(alpha = 0.3f), 
                fontSize = 10.sp, 
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Grid (10 rows x 12 cols for example)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF1C1C22), RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(12),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(120) { index ->
                        val row = index / 12 + 1
                        val col = index % 12 + 1
                        val isSelected = state.selectedSeats.contains(row to col)
                        val typeId = state.seatTypes[row to col] ?: 1L
                        
                        SeatDesignBox(
                            isSelected = isSelected,
                            color = when(typeId) {
                                2L -> Color(0xFFFFD700)
                                3L -> Color(0xFFFF69B4)
                                else -> Color.Gray
                            },
                            onClick = { viewModel.toggleSeat(row, col, selectedTypeId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeatTypeSelector(label: String, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) CyanBlue else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ),
        color = if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(16.dp).background(color, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun SeatDesignBox(isSelected: Boolean, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                if (isSelected) color else Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
    )
}
