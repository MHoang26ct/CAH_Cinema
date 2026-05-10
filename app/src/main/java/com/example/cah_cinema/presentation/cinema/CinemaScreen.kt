package com.example.cah_cinema.presentation.cinema

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.cah_cinema.domain.model.Cinema
import com.example.cah_cinema.presentation.component.FullScreenLoading
import com.example.cah_cinema.ui.theme.CyanBlue
import com.example.cah_cinema.ui.theme.TextGray

@Composable
fun CinemaScreen(
    viewModel: CinemaViewModel = viewModel(),
    onCinemaClick: (String) -> Unit = {}
) {
    // BACKEND NOTE: Observe cinema state and handle loading/error states if needed
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color(0xFF13131A),
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Header: MUA VÉ
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF2D2D35).copy(alpha = 0.5f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "MUA VÉ",
                            color = CyanBlue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                CinemaSearchBar(
                    query = state.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cinema List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.cinemas) { cinema ->
                        CinemaItemCard(
                            cinema = cinema,
                            onClick = { onCinemaClick(cinema.id) }
                        )
                    }
                }
            }
        }

        if (state.isLoading) {
            FullScreenLoading()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinemaSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp)),
        placeholder = {
            Text(text = "Tìm rạp", color = TextGray, fontSize = 14.sp)
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextGray)
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF2D2D35),
            unfocusedContainerColor = Color(0xFF2D2D35),
            disabledContainerColor = Color(0xFF2D2D35),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = CyanBlue
        ),
        singleLine = true
    )
}

@Composable
fun CinemaItemCard(
    cinema: Cinema,
    onClick: () -> Unit
) {
    // BACKEND NOTE: When a cinema is clicked, you might want to navigate 
    // to a screen showing showtimes for this specific cinema.
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF2D2D35).copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cinema.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(106.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = cinema.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = CyanBlue,
                        modifier = Modifier.size(14.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = cinema.address,
                        color = TextGray,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = CyanBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = cinema.phone,
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
