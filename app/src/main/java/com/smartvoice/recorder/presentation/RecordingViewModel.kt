package com.smartvoice.recorder.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartvoice.recorder.data.database.AppDatabase
import com.smartvoice.recorder.data.model.Recording
import com.smartvoice.recorder.domain.ai.SummarizationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecordingViewModel(context: Context) : ViewModel() {
    private val db = AppDatabase.getDatabase(context)
    private val recordingDao = db.recordingDao()
    private val summarizationService = SummarizationService()

    private val _recordings = MutableStateFlow<List<Recording>>(emptyList())
    val recordings: StateFlow<List<Recording>> = _recordings

    private val _selectedRecording = MutableStateFlow<Recording?>(null)
    val selectedRecording: StateFlow<Recording?> = _selectedRecording

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        viewModelScope.launch {
            recordingDao.getAllRecordings().collect { recordings ->
                _recordings.value = recordings
            }
        }
    }

    fun saveRecording(recording: Recording) {
        viewModelScope.launch {
            recordingDao.insertRecording(recording)
        }
    }

    fun updateRecording(recording: Recording) {
        viewModelScope.launch {
            recordingDao.updateRecording(recording)
        }
    }

    fun deleteRecording(recording: Recording) {
        viewModelScope.launch {
            recordingDao.deleteRecording(recording)
        }
    }

    fun selectRecording(recording: Recording) {
        _selectedRecording.value = recording
    }

    fun searchRecordings(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isEmpty()) {
                recordingDao.getAllRecordings().collect { recordings ->
                    _recordings.value = recordings
                }
            } else {
                recordingDao.searchRecordings(query).collect { recordings ->
                    _recordings.value = recordings
                }
            }
        }
    }

    fun summarizeRecording(recording: Recording) {
        viewModelScope.launch {
            if (recording.transcript.isNotEmpty()) {
                val result = summarizationService.summarizeTranscript(recording.transcript)
                val updatedRecording = recording.copy(
                    summary = result.summary,
                    keyPoints = result.keyPoints.joinToString("\n• "),
                    actionItems = summarizationService.formatActionItems(result.actionItems),
                    isSummarizing = false
                )
                updateRecording(updatedRecording)
                _selectedRecording.value = updatedRecording
            }
        }
    }
}

class RecordingViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordingViewModel::class.java)) {
            return RecordingViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
