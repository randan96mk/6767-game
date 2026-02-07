package com.game.winner6767

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val winnerText = findViewById<TextView>(R.id.winnerText)
        val scoreResultText = findViewById<TextView>(R.id.scoreResultText)
        val winnerNumber = findViewById<TextView>(R.id.winnerNumber)
        val playAgainButton = findViewById<Button>(R.id.playAgainButton)
        val homeButton = findViewById<Button>(R.id.homeButton)

        val score = intent.getIntExtra(GameActivity.EXTRA_SCORE, 0)

        winnerNumber.text = getString(R.string.winner_number)
        winnerText.text = getString(R.string.winner_message)
        scoreResultText.text = getString(R.string.final_score, score)

        playAgainButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
