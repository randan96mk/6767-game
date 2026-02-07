package com.game.winner6767.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.winner6767.ui.theme.ScoreCardBackground
import com.game.winner6767.ui.theme.ScoreCardText
import com.game.winner6767.ui.theme.ScoreCardValue

@Composable
fun ScoreCard(label: String, score: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .widthIn(min = 80.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(ScoreCardBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = ScoreCardText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = score.toString(),
            color = ScoreCardValue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
