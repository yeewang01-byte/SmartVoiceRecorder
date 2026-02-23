package com.smartvoice.recorder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Edit
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
import com.smartvoice.recorder.domain.ai.ActionItemResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun DetailScreen(
    recording: Recording,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onMenuClick: () -> Unit,
    onSummarize: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(stringResource(R.string.transcript), stringResource(R.string.summary))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.bg_primary))
    ) {
        // Status Bar
        RecordingStatusBar()

        // Top Navigation
        DetailTopBar(
            onBackClick = onBackClick,
            onMenuClick = onMenuClick
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Audio Info Card
            item {
                AudioInfoCard(recording = recording)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Tab Navigation
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(colorResource(R.color.bg_muted), RoundedCornerShape(12.dp)),
                    containerColor = colorResource(R.color.bg_muted),
                    indicator = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTab == index)
                                        colorResource(R.color.text_primary)
                                    else
                                        colorResource(R.color.text_secondary),
                                    fontWeight = if (selectedTab == index)
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = if (selectedTab == index) {
                                Modifier.background(
                                    Color.White,
                                    RoundedCornerShape(8.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Content based on selected tab
            item {
                when (selectedTab) {
                    0 -> {
                        // Transcript Tab
                        if (recording.transcript.isEmpty()) {
                            if (recording.isTranscribing) {
                                TranscribingPlaceholder()
                            } else {
                                EmptyTranscriptPlaceholder(onSummarize = onSummarize)
                            }
                        } else {
                            TranscriptContent(transcript = recording.transcript)
                        }
                    }
                    1 -> {
                        // Summary Tab
                        if (recording.summary.isEmpty()) {
                            EmptySummaryPlaceholder(onSummarize = onSummarize)
                        } else {
                            SummaryContent(
                                recording = recording,
                                onSummarize = onSummarize
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Action Buttons
        DetailActionButtons(
            onShareClick = onShareClick,
            onEditClick = onEditClick,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun DetailTopBar(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable(onClick = onBackClick),
            shape = RoundedCornerShape(12.dp),
            color = colorResource(R.color.bg_surface),
            shadowElevation = 0.dp
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Back",
                modifier = Modifier
                    .size(44.dp)
                    .padding(8.dp),
                tint = colorResource(R.color.text_primary)
            )
        }

        Surface(
            modifier = Modifier
                .size(44.dp)
                .clickable(onClick = onMenuClick),
            shape = RoundedCornerShape(12.dp),
            color = colorResource(R.color.bg_surface),
            shadowElevation = 0.dp
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu",
                modifier = Modifier
                    .size(44.dp)
                    .padding(8.dp),
                tint = colorResource(R.color.text_primary)
            )
        }
    }
}

@Composable
private fun AudioInfoCard(recording: Recording) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = colorResource(R.color.bg_surface),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recording.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_primary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetaInfo(
                    label = stringResource(R.string.duration),
                    value = "${recording.duration / 60}分${recording.duration % 60}秒"
                )
                MetaInfo(
                    label = stringResource(R.string.file_size),
                    value = "${recording.fileSize / 1024 / 1024} MB"
                )
                MetaInfo(
                    label = stringResource(R.string.created_time),
                    value = getTimeString(recording.createdAt)
                )
            }
        }
    }
}

@Composable
private fun MetaInfo(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = colorResource(R.color.text_tertiary)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            color = colorResource(R.color.text_primary),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TranscriptContent(transcript: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = colorResource(R.color.bg_surface),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.transcript),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_primary)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = transcript,
                fontSize = 14.sp,
                color = colorResource(R.color.text_primary),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun SummaryContent(recording: Recording, onSummarize: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Key Points Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = colorResource(R.color.accent_light),
            shadowElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.key_points),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.accent_primary)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recording.keyPoints.ifEmpty { "暂无要点" },
                    fontSize = 13.sp,
                    color = colorResource(R.color.text_primary)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Items Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = colorResource(R.color.bg_elevated),
            shadowElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.action_items),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.text_primary)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = recording.actionItems.ifEmpty { "暂无行动项" },
                    fontSize = 13.sp,
                    color = colorResource(R.color.text_primary)
                )
            }
        }
    }
}

@Composable
private fun TranscribingPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(100.dp)
            .background(colorResource(R.color.bg_surface), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "转写中...",
            fontSize = 14.sp,
            color = colorResource(R.color.text_secondary)
        )
    }
}

@Composable
private fun EmptyTranscriptPlaceholder(onSummarize: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(100.dp)
            .background(colorResource(R.color.bg_surface), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onSummarize) {
            Text("生成转写")
        }
    }
}

@Composable
private fun EmptySummaryPlaceholder(onSummarize: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(100.dp)
            .background(colorResource(R.color.bg_surface), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onSummarize) {
            Text("生成摘要")
        }
    }
}

@Composable
private fun DetailActionButtons(
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Share Button
        Button(
            onClick = onShareClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.bg_surface)
            ),
            border = BorderStroke(1.dp, colorResource(R.color.border_subtle))
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(R.string.share),
                tint = colorResource(R.color.text_primary),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.share),
                color = colorResource(R.color.text_primary)
            )
        }

        // Edit Button
        Button(
            onClick = onEditClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.accent_primary)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.edit),
                color = Color.White
            )
        }
    }
}
