package com.smartvoice.recorder.domain.audio

import kotlin.math.log10
import kotlin.math.sqrt

class SpectrumAnalyzer {
    private val fftBuffer = FloatArray(2048)
    private val magnitudes = FloatArray(10)

    /**
     * Convert PCM audio data to frequency spectrum
     * Returns 10 frequency bands (0-32px height)
     */
    fun analyzeAudio(pcmData: ByteArray): FloatArray {
        // Simplified spectrum calculation
        // In real implementation, would use FFT
        return calculateSpectrumBands(pcmData)
    }

    private fun calculateSpectrumBands(pcmData: ByteArray): FloatArray {
        val bands = FloatArray(10)
        val chunkSize = pcmData.size / 10

        for (i in 0 until 10) {
            val start = i * chunkSize
            val end = minOf(start + chunkSize, pcmData.size)
            var sum = 0f
            
            for (j in start until end step 2) {
                if (j + 1 < pcmData.size) {
                    val sample = ((pcmData[j + 1].toInt() shl 8) or (pcmData[j].toInt() and 0xFF)).toShort()
                    sum += abs(sample) / 32768f
                }
            }
            
            val average = sum / (end - start)
            // Map to height 6-32px
            bands[i] = 6 + (average * 26)
        }

        return bands
    }

    fun calculateAmplitude(pcmData: ByteArray): Float {
        var sum = 0f
        for (i in pcmData.indices step 2) {
            if (i + 1 < pcmData.size) {
                val sample = ((pcmData[i + 1].toInt() shl 8) or (pcmData[i].toInt() and 0xFF)).toShort()
                sum += sample * sample
            }
        }
        val mean = sum / (pcmData.size / 2)
        val rms = sqrt(mean)
        return minOf(rms / 32768f, 1.0f)
    }

    fun calculateDecibels(amplitude: Float): Float {
        return if (amplitude > 0) {
            20 * log10(amplitude)
        } else {
            -80f
        }
    }
}
