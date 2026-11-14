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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.UseCase.getHeartRateProgress
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.getColour
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.R

@Composable
fun StatsComposable(text: String, icon: Int, statCount: String , triColor:Boolean?=null) {
    var context = LocalContext.current
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(27.dp))
            .background(
               color = BlackCardBackground
            )
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(27.dp)
            )
            .padding(horizontal = 18.dp, vertical = 22.dp)
            .height(IntrinsicSize.Min)
            .clickable(
                enabled = false,
                onClickLabel = "",
                onClick = {
                },
                role = null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
              //  .padding( end = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                    fontSize = 16.sp,
                    color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                )
            )

            Icon(
                painter = painterResource(id = icon),
                contentDescription = "StatsIcon",
                modifier = Modifier.size(26.dp),
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = statCount.toString(),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontSize = 24.sp,
                color = if (triColor == true){
                    getColour(
                        getHeartRateProgress(statCount.toFloat().toInt())




                        , context )
                }else{
                   colorResource(com.hb.dialog.R.color.dialog_white)
                }
            )
        )

    }
}




@Preview
@Composable
fun StatsComposablePreview() {
    StatsComposable(text = "Punches", icon = R.drawable.home, statCount = "750")
}