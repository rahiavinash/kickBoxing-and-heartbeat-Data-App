package com.app.kickboxing.Activitys.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.app.kickboxing.R

@Composable
fun CustomProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color._gray)) // Background track
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress) // Fill based on progress
                .fillMaxHeight()
                .background(colorResource(R.color.colorRed)) // Foreground progress
        )
    }
}
