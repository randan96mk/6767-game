package com.mkggames.puzzle2048.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(context: Context) {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val sampleRate = 22050

    // Pre-computed buffers for different merge tiers
    private val mergeBuffers = mutableMapOf<Int, ShortArray>()

    init {
        // Pre-generate short pop/ping sounds for each merge tier
        // Higher tile values get higher-pitched, brighter sounds
        val tiers = mapOf(
            4 to 440.0,
            8 to 494.0,
            16 to 523.0,
            32 to 587.0,
            64 to 659.0,
            128 to 698.0,
            256 to 784.0,
            512 to 880.0,
            1024 to 988.0,
            2048 to 1175.0
        )
        for ((value, freq) in tiers) {
            mergeBuffers[value] = generateTone(freq, 80, 0.25f)
        }
    }

    fun playMergeSound(tileValue: Int) {
        // Find the closest tier buffer
        val buffer = mergeBuffers[tileValue]
            ?: mergeBuffers.entries
                .filter { it.key <= tileValue }
                .maxByOrNull { it.key }?.value
            ?: mergeBuffers[4]
            ?: return

        Thread {
            try {
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()

                Thread.sleep(120)
                audioTrack.release()
            } catch (_: Exception) {
                // Silently ignore audio errors
            }
        }.start()
    }

    private fun generateTone(frequency: Double, durationMs: Int, volume: Float): ShortArray {
        val numSamples = (sampleRate * durationMs / 1000.0).toInt()
        val buffer = ShortArray(numSamples)
        val fadeLen = numSamples / 8

        for (i in 0 until numSamples) {
            val angle = 2.0 * PI * i / (sampleRate / frequency)
            // Envelope: quick attack, smooth decay
            val envelope = when {
                i < fadeLen -> i.toFloat() / fadeLen
                i > numSamples - fadeLen -> (numSamples - i).toFloat() / fadeLen
                else -> 1f
            }
            val sample = sin(angle) * Short.MAX_VALUE * volume * envelope
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }
}
