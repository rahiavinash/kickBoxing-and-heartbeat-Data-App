package com.app.kickboxing.Activitys.presentation.Ui.home.ui

import android.content.Context
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.app.kickboxing.R


@Composable
fun ProgressBar(progress: Float, isThriceColor: Boolean? = null) {
    val context = LocalContext.current
    val color = getColour(progress, context)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color._gray))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isThriceColor == true) {
                        color
                    } else {
                        colorResource(R.color.colorRed)
                    }
                )
        )
    }
}


fun getColour(heartRate: Float , context: Context): androidx.compose.ui.graphics.Color {
    Log.e("jfkldjsfjd", heartRate.toString())
    return when {

        heartRate <= 0.5f -> Color(ContextCompat.getColor(context, R.color.colorRed))
        heartRate in 0.5f..0.6f -> Color(ContextCompat.getColor(context, R.color.colorYellow))
        heartRate in 0.6f..0.8f ->Color(ContextCompat.getColor(context, R.color.colorGreen))
        heartRate <= 0.9f->Color(ContextCompat.getColor(context, R.color.colorYellow))
        heartRate > 1.0f ->  Color(ContextCompat.getColor(context, R.color.colorRed))
        else ->  Color(ContextCompat.getColor(context, R.color.colorBlue))
    }
}