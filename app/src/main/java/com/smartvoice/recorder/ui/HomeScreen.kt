package com.smartvoice.recorder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartvoice.recorder.R
import com.smartvoice.recorder.data.model.Recording
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    recordings: List<Recording>,
    onRecordingClick: (Recording) -> Unit,
    onNewRecordingClick: () -> Unit,
    onSearchChange: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.bg_primary))
    ) {
        // Status Bar
        HomeStatusBar()

        // Header
        HomeHeader()

        // Search Box
        HomeSearchBox(
            searchQuery = searchQuery,
            onSearchChange = { newQuery ->
                searchQuery = newQuery
                onSearchChange(newQuery)
            }
        )

        // Recordings List
        if (recordings.isEmpty()) {
            EmptyRecordingsPlaceholder()
        } else {
            RecordingsList(
                recordings = recordings,
                onRecordingClick = onRecordingClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Floating Action Button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        FloatingActionButton(
            onClick = onNewRecordingClick,
            containerColor = colorResource(R.color.accent_primary),
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = stringResource(R.string.start_recording),
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun HomeStatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.bg_primary))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getFormattedTime(),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_primary)
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("📶", fontSize = 12.sp)
            Text("🔋", fontSize = 12.sp)
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.my_recordings),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_primary)
        )
        
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable { },
            shape = RoundedCornerShape(12.dp),
            color = colorResource(R.color.bg_surface),
            shadowElevation = 0.dp
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Notifications",
                modifier = Modifier
                    .size(44.dp)
                    .padding(8.dp),
                tint = colorResource(R.color.text_secondary)
            )
        }
    }
}

@Composable
private fun HomeSearchBox(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search_recordings),
                color = colorResource(R.color.text_tertiary)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = colorResource(R.color.text_tertiary)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = colorResource(R.color.bg_surface),
            focusedContainerColor = colorResource(R.color.bg_surface),
            unfocusedBorderColor = colorResource(R.color.border_subtle),
            focusedBorderColor = colorResource(R.color.accent_primary)
        )
    )
}

@Composable
private fun RecordingsList(
    recordings: List<Recording>,
    onRecordingClick: (Recording) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(colorResource(R.color.bg_surface), RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.recent_recordings),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_primary),
            modifier = Modifier.padding(8.dp)
        )

        LazyColumn {
            items(recordings) { recording ->
                RecordingListItem(
                    recording = recording,
                    onClick = { onRecordingClick(recording) }
                )
            }
        }
    }
}

@Composable
private fun RecordingListItem(
    recording: Recording,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = colorResource(R.color.bg_primary),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📎 ${recording.title}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.text_primary),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${getTimeString(recording.createdAt)} | ${formatDuration(recording.duration)}",
                fontSize = 12.sp,
                color = colorResource(R.color.text_secondary)
            )
        }
    }
}

@Composable
private fun EmptyRecordingsPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📭",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "暂无录音",
            fontSize = 16.sp,
            color = colorResource(R.color.text_secondary)
        )
    }
}

private fun getFormattedTime(): String {
    val formatter = SimpleDateFormat("H:mm", Locale.getDefault())
    return formatter.format(Date())
}

private fun getTimeString(timestamp: Long): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

private fun formatDuration(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "${mins}分${secs}秒"
}
