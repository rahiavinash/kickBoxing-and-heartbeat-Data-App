package com.app.kickboxing.Activitys.presentation.Ui.home.ui.home.commonWidget

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.bottomNavigationBar.data.BottomNavItem
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.R

@Composable
fun OverViewComposable(
    text: String, icon: Int, statCount: Int, boxHeading: String,
    navController: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(28.dp))
            .background(
                BlackCardBackground

            )
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(horizontal = 14.dp, vertical = 22.dp)
            .height(IntrinsicSize.Min)
            .clickable(
                onClickLabel = "",
                onClick =  {
                    navController()

                },
                role = null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                    fontSize = 15.sp,
                    color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "StatsIcon",
                modifier = Modifier.size(26.dp),
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = statCount.toString(),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontSize = 20.sp,
                color = colorResource(id = com.hb.dialog.R.color.dialog_white)
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = boxHeading.toString(),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontSize = 14.sp,
                color = colorResource(id = com.hb.dialog.R.color.dialog_white)
            )
        )
    }
}