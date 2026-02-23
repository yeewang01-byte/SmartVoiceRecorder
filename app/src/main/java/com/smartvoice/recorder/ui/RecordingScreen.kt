package com.smartvoice.recorder.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartvoice.recorder.R
import com.smartvoice.recorder.presentation.RecordingState

@Composable
fun RecordingScreen(
    recordingState: RecordingState,
    recordingDuration: Long,
    spectrumData: FloatArray,
    waveformAmplitude: Float,
    isTranscribing: Boolean,
    onStartRecording: () -> Unit,
    onPauseRecording: () -> Unit,
    onResumeRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    onBackClick: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.bg_primary))
    ) {
        // Status Bar
        RecordingStatusBar()

        // Back Button
        BackButtonRow(onBackClick = {
            showConfirmDialog = true
        })

        // Main Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Audio Wave Ring Animation
            AudioWaveRing(amplitude = waveformAmplitude)

            Spacer(modifier = Modifier.height(32.dp))

            // Timer
            RecordingTimer(duration = recordingDuration)

            Spacer(modifier = Modifier.height(12.dp))

            // Status Label
            Text(
                text = stringResource(R.string.recording) + " • " + stringResource(R.string.audio_clear),
                fontSize = 14.sp,
                color = colorResource(R.color.text_secondary)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Spectrum Bars
            SpectrumBars(data = spectrumData)

            Spacer(modifier = Modifier.height(48.dp))

            // AI Transcribing Indicator
            if (isTranscribing) {
                AITranscribingBox()
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Control Buttons
        RecordingControlButtons(
            recordingState = recordingState,
            onStartRecording = onStartRecording,
            onPauseRecording = onPauseRecording,
            onResumeRecording = onResumeRecording,
            onStopRecording = onStopRecording,
            onCancelRecording = onCancelRecording,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }

    // Confirm Dialog
    if (showConfirmDialog) {
        ConfirmDiscardDialog(
            onConfirm = {
                onCancelRecording()
                onBackClick()
                showConfirmDialog = false
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
}

@Composable
private fun RecordingStatusBar() {
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
private fun BackButtonRow(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
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
    }
}

@Composable
private fun AudioWaveRing(amplitude: Float) {
    val animatedAmplitude by animateFloatAsState(
        targetValue = amplitude,
        animationSpec = tween(300)
    )
    
    // Outer Ring
    Surface(
        modifier = Modifier
            .size(220.dp)
            .scale(0.6f + (animatedAmplitude * 0.55f))
            .clip(CircleShape),
        color = Color.Transparent,
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .border(
                    width = 6.dp,
                    color = colorResource(R.color.accent_primary),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun RecordingTimer(duration: Long) {
    val minutes = duration / 60000
    val seconds = (duration % 60000) / 1000
    
    Text(
        text = String.format("%02d:%02d", minutes, seconds),
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        color = colorResource(R.color.text_primary)
    )
}

@Composable
private fun SpectrumBars(data: FloatArray) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        for (i in 0 until minOf(10, data.size)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((data[i] / 32f).coerceIn(6f, 32f).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colorResource(R.color.accent_primary))
            )
        }
    }
}

@Composable
private fun AITranscribingBox() {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(R.color.bg_surface)),
        shape = RoundedCornerShape(12.dp),
        color = colorResource(R.color.bg_surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Loading animation placeholder
            Text("⏳", fontSize = 24.sp)
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.ai_transcribing),
                    fontSize = 14.sp,
                    color = colorResource(R.color.text_primary),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Button(
                onClick = { /* Background transcribe */ },
                modifier = Modifier
                    .width(120.dp)
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.accent_primary)
                ),
                contentPadding = PaddingValues(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.background_transcribe),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun RecordingControlButtons(
    recordingState: RecordingState,
    onStartRecording: () -> Unit,
    onPauseRecording: () -> Unit,
    onResumeRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cancel Button
        Button(
            onClick = onCancelRecording,
            modifier = Modifier
                .size(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.bg_surface)
            ),
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(1.dp, colorResource(R.color.border_subtle))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.cancel_recording),
                tint = colorResource(R.color.text_primary)
            )
        }

        // Stop Button (Main)
        Button(
            onClick = onStopRecording,
            modifier = Modifier
                .width(72.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.accent_primary)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = stringResource(R.string.stop_recording),
                tint = Color.White
            )
        }

        // Pause/Resume Button
        Button(
            onClick = if (recordingState == RecordingState.RECORDING) onPauseRecording else onResumeRecording,
            modifier = Modifier
                .size(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.bg_surface)
            ),
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(1.dp, colorResource(R.color.border_subtle))
        ) {
            Icon(
                imageVector = if (recordingState == RecordingState.RECORDING) 
                    Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (recordingState == RecordingState.RECORDING)
                    stringResource(R.string.pause_recording) else stringResource(R.string.resume_recording),
                tint = colorResource(R.color.text_primary)
            )
        }
    }
}

@Composable
private fun ConfirmDiscardDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("放弃录音？") },
        text = { Text("未保存的录音将被丢弃") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("确认")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
