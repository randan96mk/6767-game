package com.game.winner6767

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var timerText: TextView
    private lateinit var gridLayout: GridLayout
    private lateinit var targetText: TextView

    private var score = 0
    private var targetNumber = 0
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        scoreText = findViewById(R.id.scoreText)
        timerText = findViewById(R.id.timerText)
        gridLayout = findViewById(R.id.gridLayout)
        targetText = findViewById(R.id.targetText)

        startNewRound()
        startTimer()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                timerText.text = getString(R.string.time_format, seconds)
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    private fun startNewRound() {
        gridLayout.removeAllViews()

        // Generate target: always include 6767 as one of the options
        val numbers = mutableListOf<Int>()
        targetNumber = 6767
        numbers.add(6767)

        // Add 8 random distractor numbers (4-digit)
        while (numbers.size < 9) {
            val rand = Random.nextInt(1000, 9999)
            if (rand != 6767 && rand !in numbers) {
                numbers.add(rand)
            }
        }
        numbers.shuffle()

        targetText.text = getString(R.string.find_target, targetNumber)

        for (number in numbers) {
            val button = Button(this).apply {
                text = number.toString()
                textSize = 18f
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED)
                    setMargins(8, 8, 8, 8)
                }
                setOnClickListener {
                    onNumberClicked(number)
                }
            }
            gridLayout.addView(button)
        }
    }

    private fun onNumberClicked(number: Int) {
        if (number == targetNumber) {
            score += 100
            scoreText.text = getString(R.string.score_format, score)
            startNewRound()
        } else {
            score = maxOf(0, score - 25)
            scoreText.text = getString(R.string.score_format, score)
        }
    }

    private fun endGame() {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(EXTRA_SCORE, score)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    companion object {
        const val EXTRA_SCORE = "extra_score"
    }
}
