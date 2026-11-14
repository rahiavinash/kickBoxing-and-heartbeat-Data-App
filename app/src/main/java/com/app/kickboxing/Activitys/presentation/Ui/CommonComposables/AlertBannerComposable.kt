package com.app.kickboxing.Activitys.presentation.Ui.CommonComposables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.AlertSuccessGreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed

@Composable
fun AlertBanner(
    alertState: AlertState,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (alertState.type) {
        AlertType.SUCCESS -> AlertSuccessGreen
        AlertType.ERROR -> ButtonRed
    }

    val icon = when (alertState.type) {
        AlertType.SUCCESS -> Icons.Default.CheckCircle
        AlertType.ERROR -> Icons.Default.Warning
    }

    AnimatedVisibility(
        visible = alertState.isVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
            .padding(top = 32.dp) // avoid status bar overlap
            .zIndex(1f)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = backgroundColor,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = alertState.message,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = { onDismiss?.invoke() }) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

            }
        }
    }
}



@Composable
fun AlertHost(
    alertState: AlertState,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (alertState.isVisible) {
        LaunchedEffect(alertState.message) {
            delay(3000)
            onDismiss?.invoke()
        }
    }

    AlertBanner(alertState = alertState, onDismiss = onDismiss, modifier = modifier)
}


@Preview
@Composable
fun AlertBannerSuccessPreview(){
    val alertState = AlertState(
        message = "This is a success alert!",
        type = AlertType.SUCCESS,
        isVisible = true
    )
    AlertHost(alertState, modifier = Modifier, onDismiss = {})
}

@Preview
@Composable
fun AlertBannerErrorPreview(){
    val alertState = AlertState(
        message = "This is an error alert!",
        type = AlertType.ERROR,
        isVisible = true
    )
    AlertHost(alertState, modifier = Modifier, onDismiss = {})
}


data class AlertState(
    val message: String = "",
    val type: AlertType = AlertType.SUCCESS,
    val isVisible: Boolean = false
)


enum class AlertType {
    SUCCESS, ERROR
}