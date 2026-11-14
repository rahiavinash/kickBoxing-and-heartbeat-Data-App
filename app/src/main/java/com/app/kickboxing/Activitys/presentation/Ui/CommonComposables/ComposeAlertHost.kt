package com.app.kickboxing.Activitys.presentation.Ui.CommonComposables

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Window
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.AlertSuccessGreen
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.RedColor

@Composable
fun ComposeAlertHost(
    alertState: AlertStateCompose,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier

) {
    if (alertState.isVisible) {
        val backgroundColor = when (alertState.type) {
            AlertType.SUCCESS -> AlertSuccessGreen
            AlertType.ERROR -> ButtonRed
        }
        SetStatusBarColor(color = backgroundColor, darkIcons = false)

        val icon = when (alertState.type) {
            AlertType.SUCCESS -> Icons.Default.CheckCircle
            AlertType.ERROR -> Icons.Default.Warning
        }

        AnimatedVisibility(
            visible = alertState.isVisible,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = modifier
                   .padding(top = 6.dp)
                //      .systemBarsPadding()
                .zIndex(1f)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                // .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())

                ,
                color = backgroundColor,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 35.dp , bottom = 10.dp , start = 12.dp , end = 20.dp )
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
}

@SuppressLint("ContextCastToActivity")
@Composable
fun SetStatusBarColor(color: Color, darkIcons: Boolean = false) {
    val activity = LocalContext.current as? Activity ?: return

    SideEffect {
        val window: Window = activity.window
        window.statusBarColor = color.toArgb()
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        // insetsController.isAppearanceLightStatusBars = darkIcons.not()
        insetsController.isAppearanceLightStatusBars = false
    }
}

data class AlertStateCompose(

    val message: String = "",
    val type: AlertType = AlertType.SUCCESS,
    val isVisible: Boolean = false
)


