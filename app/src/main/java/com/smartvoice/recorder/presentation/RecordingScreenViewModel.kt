package com.smartvoice.recorder.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartvoice.recorder.domain.audio.AudioRecorderManager
import com.smartvoice.recorder.domain.audio.SpectrumAnalyzer
import com.smartvoice.recorder.domain.ai.TranscriptionService
import com.smartvoice.recorder.data.model.Recording
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class RecordingState {
    IDLE, RECORDING, PAUSED, STOPPING
}

class RecordingScreenViewModel(context: Context) : ViewModel() {
    private val audioRecorder = AudioRecorderManager(context)
    private val spectrumAnalyzer = SpectrumAnalyzer()
    private val transcriptionService = TranscriptionService()

    private val _recordingState = MutableStateFlow(RecordingState.IDLE)
    val recordingState: StateFlow<RecordingState> = _recordingState

    private val _recordingDuration = MutableStateFlow(0L)
    val recordingDuration: StateFlow<Long> = _recordingDuration

    private val _spectrumData = MutableStateFlow(FloatArray(10))
    val spectrumData: StateFlow<FloatArray> = _spectrumData

    private val _waveformAmplitude = MutableStateFlow(0.5f)
    val waveformAmplitude: StateFlow<Float> = _waveformAmplitude

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing

    init {
        // Could set up spectrum analysis updates here
    }

    fun startRecording(): File? {
        return try {
            val audioDir = audioRecorder.getAudioDirectory()
            val timestamp = System.currentTimeMillis().toString()
            val outputFile = File(audioDir, "recording_$timestamp.m4a")

            if (audioRecorder.startRecording(outputFile)) {
                _recordingState.value = RecordingState.RECORDING
                // Simulate spectrum updates
                viewModelScope.launch {
                    while (audioRecorder.isRecordingActive()) {
                        _recordingDuration.value = audioRecorder.getRecordingDuration()
                        // Simulate spectrum data
                        val spectrum = FloatArray(10) { (Math.random() * 32 + 6).toFloat() }
                        _spectrumData.value = spectrum
                        
                        val amplitude = (Math.random() * 1.0).toFloat()
                        _waveformAmplitude.value = amplitude
                        
                        kotlinx.coroutines.delay(100)
                    }
                }
                outputFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun pauseRecording() {
        audioRecorder.pauseRecording()
        _recordingState.value = RecordingState.PAUSED
    }

    fun resumeRecording() {
        audioRecorder.resumeRecording()
        _recordingState.value = RecordingState.RECORDING
    }

    fun stopRecording(): Long {
        val duration = audioRecorder.stopRecording()
        _recordingState.value = RecordingState.STOPPING
        return duration
    }

    fun cancelRecording() {
        audioRecorder.cancelRecording()
        _recordingState.value = RecordingState.IDLE
        _recordingDuration.value = 0L
    }

    fun transcribeRecording(audioFile: File) {
        _isTranscribing.value = true
        viewModelScope.launch {
            val transcript = transcriptionService.transcribeAudio(audioFile.absolutePath)
            // Result would be passed back via callback or state update
            _isTranscribing.value = false
        }
    }

    fun formatDuration(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun generateRecordingTitle(): String {
        val formatter = SimpleDateFormat("yyyy年M月d日 HH:mm", Locale.CHINESE)
        return "录音 - ${formatter.format(Date())}"
    }
}

class RecordingScreenViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordingScreenViewModel::class.java)) {
            return RecordingScreenViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
