package com.smartvoice.recorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import com.smartvoice.recorder.presentation.RecordingViewModel
import com.smartvoice.recorder.presentation.RecordingViewModelFactory
import com.smartvoice.recorder.presentation.RecordingScreenViewModel
import com.smartvoice.recorder.presentation.RecordingScreenViewModelFactory
import com.smartvoice.recorder.presentation.RecordingState
import com.smartvoice.recorder.ui.HomeScreen
import com.smartvoice.recorder.ui.RecordingScreen
import com.smartvoice.recorder.ui.DetailScreen
import androidx.compose.ui.res.colorResource
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

enum class AppScreen {
    HOME, RECORDING, DETAIL
}

class MainActivity : ComponentActivity() {
    private lateinit var recordingViewModel: RecordingViewModel
    private lateinit var recordingScreenViewModel: RecordingScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions
        requestAudioPermissions()

        // Initialize ViewModels
        recordingViewModel = ViewModelProvider(
            this,
            RecordingViewModelFactory(this)
        )[RecordingViewModel::class.java]

        recordingScreenViewModel = ViewModelProvider(
            this,
            RecordingScreenViewModelFactory(this)
        )[RecordingScreenViewModel::class.java]

        setContent {
            AppContent()
        }
    }

    @Composable
    private fun AppContent() {
        var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

        val recordings by recordingViewModel.recordings.collectAsState()
        val selectedRecording by recordingViewModel.selectedRecording.collectAsState()
        val recordingState by recordingScreenViewModel.recordingState.collectAsState()
        val recordingDuration by recordingScreenViewModel.recordingDuration.collectAsState()
        val spectrumData by recordingScreenViewModel.spectrumData.collectAsState()
        val waveformAmplitude by recordingScreenViewModel.waveformAmplitude.collectAsState()
        val isTranscribing by recordingScreenViewModel.isTranscribing.collectAsState()

        Surface(
            color = colorResource(R.color.bg_primary)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        recordings = recordings,
                        onRecordingClick = { recording ->
                            recordingViewModel.selectRecording(recording)
                            currentScreen = AppScreen.DETAIL
                        },
                        onNewRecordingClick = {
                            currentScreen = AppScreen.RECORDING
                            recordingScreenViewModel.startRecording()
                        },
                        onSearchChange = { query ->
                            recordingViewModel.searchRecordings(query)
                        }
                    )
                }

                AppScreen.RECORDING -> {
                    RecordingScreen(
                        recordingState = recordingState,
                        recordingDuration = recordingDuration,
                        spectrumData = spectrumData,
                        waveformAmplitude = waveformAmplitude,
                        isTranscribing = isTranscribing,
                        onStartRecording = {
                            recordingScreenViewModel.startRecording()
                        },
                        onPauseRecording = {
                            recordingScreenViewModel.pauseRecording()
                        },
                        onResumeRecording = {
                            recordingScreenViewModel.resumeRecording()
                        },
                        onStopRecording = {
                            val duration = recordingScreenViewModel.stopRecording()
                            val title = recordingScreenViewModel.generateRecordingTitle()
                            
                            // Save to database
                            val newRecording = com.smartvoice.recorder.data.model.Recording(
                                title = title,
                                audioFilePath = "", // Would be set from file created
                                duration = duration / 1000
                            )
                            recordingViewModel.saveRecording(newRecording)
                            currentScreen = AppScreen.HOME
                        },
                        onCancelRecording = {
                            recordingScreenViewModel.cancelRecording()
                            currentScreen = AppScreen.HOME
                        },
                        onBackClick = {
                            currentScreen = AppScreen.HOME
                        }
                    )
                }

                AppScreen.DETAIL -> {
                    selectedRecording?.let { recording ->
                        DetailScreen(
                            recording = recording,
                            onBackClick = {
                                currentScreen = AppScreen.HOME
                            },
                            onShareClick = {
                                shareRecording(recording)
                            },
                            onEditClick = {
                                // Would open edit dialog
                            },
                            onMenuClick = {
                                // Would open menu
                            },
                            onSummarize = {
                                recordingViewModel.summarizeRecording(recording)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun shareRecording(recording: com.smartvoice.recorder.data.model.Recording) {
        val intent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_SUBJECT, recording.title)
            putExtra(
                android.content.Intent.EXTRA_TEXT,
                "标题: ${recording.title}\n\n" +
                        "转写: ${recording.transcript}\n\n" +
                        "摘要: ${recording.summary}"
            )
        }
        startActivity(android.content.Intent.createChooser(intent, "分享录音"))
    }

    private fun requestAudioPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permissionsNeeded = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 1001)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // All permissions granted
            }
        }
    }
}
