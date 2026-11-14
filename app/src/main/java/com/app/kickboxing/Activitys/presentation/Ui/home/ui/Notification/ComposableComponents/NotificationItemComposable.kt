package com.app.kickboxing.Activitys.presentation.Ui.home.ui.Notification.ComposableComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.NotificationLineColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R
import java.nio.file.WatchEvent

@Composable
fun NotificationItemComposable(isSameDate: Boolean = false, date: String? = null, title: String? = null, description: String? = null, time: String? = null, onClickNotification: (() -> Unit)? = null){
    Column (
        modifier = Modifier.fillMaxWidth()
            .clickable(
                enabled = true,
                onClickLabel = "",
                onClick = {
                    onClickNotification?.invoke()                },
                role = null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ){
        Spacer(modifier = Modifier.height(32.dp))
        if(!isSameDate){
            Text(
                text = date?:"",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 20.sp,
                    color = WhiteColor
                )
            )
            Spacer(modifier = Modifier.height(40.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_notification),
                contentDescription = "TrophyIcon",
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.width(24.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title?:"",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 14.sp,
                        color = WhiteColor
                    ),
                    maxLines = 1

                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = description?:"",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                        fontSize = 14.sp,
                        color = WhiteColor
                    ),
                    maxLines = 2

                )

            }

            Text(
                text = time?:"",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    fontSize = 14.sp,
                    color = WhiteColor
                ),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row (
            modifier = Modifier.fillMaxWidth().height(1.dp)
                .background(color = NotificationLineColor)
        ){  }
    }
}

@Preview
@Composable
fun NotificationItemPreview(){
    NotificationItemComposable(isSameDate = false, date = "Today", title = "CaFit", description = "Your Duel with Alex will start today. Be well prepared to participate.", time = "02:01 PM")
}