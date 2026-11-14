package com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.ProgressBar
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R

@Composable
fun AveragePerformanceComposable(icon: Int, title: String, stats: String, progress: Float , isThriceColor:Boolean?=null) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
             BlackCardBackground
            )
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .height(IntrinsicSize.Min)
            .padding(horizontal = 18.dp, vertical = 20.dp )
            .clickable(
                enabled = false,
                onClickLabel = "",
                onClick = {
                },
                role = null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
        ,horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Performance Icon",
            modifier = Modifier.size(32.dp),
            tint = Color.Unspecified
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                        fontSize = 16.sp,
                        color = WhiteColor
                    )
                )

                Text(
                    text = stats,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                        fontSize = 12.sp,
                        color = WhiteColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                ProgressBar(progress = progress , isThriceColor=isThriceColor)
            }
        }
    }
}

@Preview
@Composable
fun AveragePerformanceComposablePreview(){
    AveragePerformanceComposable(icon = R.drawable.home, "Home", "88/1000", 0.5f)
}
