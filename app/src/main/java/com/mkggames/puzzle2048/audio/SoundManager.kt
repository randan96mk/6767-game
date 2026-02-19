package com.mkggames.puzzle2048.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

class SoundManager(context: Context) {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val sampleRate = 44100

    // Pre-computed buffers for different merge tiers
    private val mergeBuffers = mutableMapOf<Int, ShortArray>()

    init {
        // Tile 4-16:   Quick bright pop (short, snappy)
        // Tile 32-128: Warm chime with harmonics (medium)
        // Tile 256-1024: Shimmery bell with rich overtones (longer)
        // Tile 2048:   Triumphant major chord with sparkle

        mergeBuffers[4]    = generatePop(523.25, durationMs = 90, harmonics = 2, volume = 0.30f)
        mergeBuffers[8]    = generatePop(587.33, durationMs = 95, harmonics = 2, volume = 0.32f)
        mergeBuffers[16]   = generatePop(659.25, durationMs = 100, harmonics = 2, volume = 0.34f)
        mergeBuffers[32]   = generateChime(698.46, durationMs = 120, harmonics = 3, volume = 0.34f)
        mergeBuffers[64]   = generateChime(783.99, durationMs = 130, harmonics = 3, volume = 0.36f)
        mergeBuffers[128]  = generateChime(880.00, durationMs = 140, harmonics = 4, volume = 0.36f)
        mergeBuffers[256]  = generateBell(987.77, durationMs = 170, volume = 0.38f)
        mergeBuffers[512]  = generateBell(1108.73, durationMs = 190, volume = 0.38f)
        mergeBuffers[1024] = generateBell(1174.66, durationMs = 210, volume = 0.40f)
        mergeBuffers[2048] = generateTriumphChord(durationMs = 350, volume = 0.42f)
    }

    fun playMergeSound(tileValue: Int) {
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

                Thread.sleep((buffer.size * 1000L / sampleRate) + 30)
                audioTrack.release()
            } catch (_: Exception) {
                // Silently ignore audio errors
            }
        }.start()
    }

    /**
     * Quick bright pop with slight upward pitch sweep — for low tiles (4, 8, 16).
     */
    private fun generatePop(
        baseFreq: Double,
        durationMs: Int,
        harmonics: Int,
        volume: Float
    ): ShortArray {
        val numSamples = (sampleRate * durationMs / 1000.0).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples

            // Quick pitch sweep: starts 15% higher, settles to base freq
            val freq = baseFreq * (1.0 + 0.15 * (1.0 - progress).pow(3))

            // Snappy envelope: instant attack, exponential decay
            val envelope = exp(-progress * 6.0)

            var sample = 0.0
            for (h in 1..harmonics) {
                val harmAmp = 1.0 / (h * h) // Higher harmonics are quieter
                sample += sin(2.0 * PI * freq * h * t) * harmAmp
            }

            val value = (sample * Short.MAX_VALUE * volume * envelope).toInt()
            buffer[i] = value.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    /**
     * Warm chime with richer harmonics and a gentle shimmer — for mid tiles (32, 64, 128).
     */
    private fun generateChime(
        baseFreq: Double,
        durationMs: Int,
        harmonics: Int,
        volume: Float
    ): ShortArray {
        val numSamples = (sampleRate * durationMs / 1000.0).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples

            // Smooth pitch settle
            val freq = baseFreq * (1.0 + 0.08 * (1.0 - progress).pow(2))

            // Envelope: 3ms attack, smooth exponential decay
            val attackSamples = (sampleRate * 0.003).toInt()
            val attack = if (i < attackSamples) i.toDouble() / attackSamples else 1.0
            val decay = exp(-progress * 4.5)
            val envelope = attack * decay

            var sample = 0.0
            // Fundamental + harmonics with bell-like inharmonicity
            for (h in 1..harmonics) {
                val harmFreqRatio = h + h * 0.002 * h // Slight inharmonicity for warmth
                val harmAmp = 1.0 / (h.toDouble().pow(1.5))
                sample += sin(2.0 * PI * freq * harmFreqRatio * t) * harmAmp
            }

            // Add a subtle octave shimmer
            val shimmer = sin(2.0 * PI * freq * 2.0 * t) * 0.15 * exp(-progress * 8.0)
            sample += shimmer

            val value = (sample * Short.MAX_VALUE * volume * envelope).toInt()
            buffer[i] = value.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    /**
     * Shimmery bell with rich overtones and sparkle — for high tiles (256, 512, 1024).
     */
    private fun generateBell(
        baseFreq: Double,
        durationMs: Int,
        volume: Float
    ): ShortArray {
        val numSamples = (sampleRate * durationMs / 1000.0).toInt()
        val buffer = ShortArray(numSamples)

        // Bell partials: frequency ratios typical of metallic bell sounds
        val partials = doubleArrayOf(1.0, 2.0, 2.83, 3.0, 4.07, 5.4)
        val partialAmps = doubleArrayOf(1.0, 0.6, 0.4, 0.25, 0.2, 0.12)
        val partialDecays = doubleArrayOf(3.5, 5.0, 7.0, 8.0, 10.0, 12.0)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples

            // Very fast attack
            val attackSamples = (sampleRate * 0.002).toInt()
            val attack = if (i < attackSamples) i.toDouble() / attackSamples else 1.0

            var sample = 0.0
            for (p in partials.indices) {
                val partialDecay = exp(-progress * partialDecays[p])
                sample += sin(2.0 * PI * baseFreq * partials[p] * t) * partialAmps[p] * partialDecay
            }

            // Sparkle: high-frequency transient at the start
            val sparkle = sin(2.0 * PI * baseFreq * 8.0 * t) * 0.1 * exp(-progress * 25.0)
            sample += sparkle

            val value = (sample * Short.MAX_VALUE * volume * attack).toInt()
            buffer[i] = value.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    /**
     * Triumphant major chord with shimmer and long tail — for the legendary 2048 tile.
     */
    private fun generateTriumphChord(durationMs: Int, volume: Float): ShortArray {
        val numSamples = (sampleRate * durationMs / 1000.0).toInt()
        val buffer = ShortArray(numSamples)

        // C6 major chord: C6 + E6 + G6, with octave doublings
        val chordFreqs = doubleArrayOf(
            1046.50,  // C6
            1318.51,  // E6
            1567.98,  // G6
            2093.00,  // C7 (octave up for brightness)
            2637.02   // E7 (sparkle top)
        )
        val chordAmps = doubleArrayOf(1.0, 0.8, 0.7, 0.35, 0.2)
        val chordDecays = doubleArrayOf(2.5, 3.0, 3.5, 5.0, 7.0)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples

            // Fast attack
            val attackSamples = (sampleRate * 0.004).toInt()
            val attack = if (i < attackSamples) i.toDouble() / attackSamples else 1.0

            var sample = 0.0
            for (c in chordFreqs.indices) {
                val decay = exp(-progress * chordDecays[c])
                sample += sin(2.0 * PI * chordFreqs[c] * t) * chordAmps[c] * decay

                // Add a subtle 2nd harmonic per note for richness
                sample += sin(2.0 * PI * chordFreqs[c] * 2.0 * t) * chordAmps[c] * 0.15 * decay
            }

            // Rising sparkle sweep at the very start
            val sweepFreq = 2000.0 + 4000.0 * progress.pow(0.3)
            val sparkle = sin(2.0 * PI * sweepFreq * t) * 0.08 * exp(-progress * 12.0)
            sample += sparkle

            val value = (sample * Short.MAX_VALUE * volume * attack).toInt()
            buffer[i] = value.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }
}
