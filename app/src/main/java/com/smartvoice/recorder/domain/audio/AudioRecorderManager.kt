package com.smartvoice.recorder.domain.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.File
import kotlin.math.abs

class AudioRecorderManager(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var recordingStartTime = 0L
    private val audioDataCallbacks = mutableListOf<(ByteArray) -> Unit>()

    fun startRecording(outputFile: File): Boolean {
        return try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            recordingStartTime = System.currentTimeMillis()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun pauseRecording() {
        if (isRecording && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mediaRecorder?.pause()
            isRecording = false
        }
    }

    fun resumeRecording() {
        if (!isRecording && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mediaRecorder?.resume()
            isRecording = true
        }
    }

    fun stopRecording(): Long {
        return try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            System.currentTimeMillis() - recordingStartTime
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    fun cancelRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRecordingDuration(): Long {
        return if (isRecording) {
            System.currentTimeMillis() - recordingStartTime
        } else {
            0L
        }
    }

    fun isRecordingActive(): Boolean = isRecording

    fun getAudioDirectory(): File {
        val audioDir = File(context.getExternalFilesDir(null), "recordings")
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }
        return audioDir
    }

    // For spectrum visualization - collect audio samples
    fun startAudioCapture(sampleRate: Int = 44100) {
        // This would be used with AudioRecord for real-time spectrum analysis
    }
}
