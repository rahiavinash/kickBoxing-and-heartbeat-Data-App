package com.app.kickboxing.Activitys.presentation.Ui.home.ui.home.commonWidget

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
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents.SessionItem
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R

@Composable
fun PerformanceItem(
    avgPower: Double,
    avgPunchCount: Double,
    avgScore : Double,

) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .padding(horizontal = 0.dp)
            .height(IntrinsicSize.Max)
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = BlackCardBackground
            )
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 18.dp)
            .clickable(
                enabled = true,
                onClickLabel = "",
                onClick = {
                },
                role = null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),

        ) {


        Row(modifier = Modifier.fillMaxWidth()) {


            Text(
                text = "Avg Power",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = avgPower.toString(),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {


            Text(
                text = "Avg Punch Count",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = avgPunchCount.toString(),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {


            Text(
                text = "Avg Score",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = avgScore.toString(),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

        }


    }
}




@Composable
fun AverageItem(

    powerIncrease: Boolean,
    powerPercentage: Double,
    punchPercentage: Double,
    punchIncrease: Boolean,
    scorePercentage : Double,
    scoreIncrease : Boolean

) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .padding(horizontal = 0.dp)
            .height(IntrinsicSize.Max)
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = BlackCardBackground
            )
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 18.dp)
            .clickable(
                enabled = true,
                onClickLabel = "",
                onClick = {
                },
                role = null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),

        ) {


        Row(modifier = Modifier.fillMaxWidth()) {


            Text(
                text = if(powerIncrease){
                    "Average Power go up by ${powerPercentage?:0.0}%"
                }else{
                    "Average Power go down by ${powerPercentage?:0.0}%"
                },
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = if (powerIncrease) R.drawable.ic_up_arrow else R.drawable.ic_down_arrow),
                contentDescription = "StatsIcon",
                modifier = Modifier.size(16.dp),
                tint = Color.Unspecified
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {


            Text(
                text  = if (punchIncrease) {
                    "Total Punches go up by ${punchPercentage?:0.0}%"
                } else {
                    "Total Punches go down by ${punchPercentage ?: 0.0}%"
                }
                   ,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = if (punchIncrease) R.drawable.ic_up_arrow else R.drawable.ic_down_arrow),
                contentDescription = "StatsIcon",
                modifier = Modifier.size(16.dp),
                tint = Color.Unspecified
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {


            Text(
                text = if(scoreIncrease){
                    "Total Score up by ${scorePercentage?:0.0}%"
                }else{
                    "Total Score down by ${scorePercentage?:0.0}%"
                },
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = if (scoreIncrease) R.drawable.ic_up_arrow else R.drawable.ic_down_arrow),
                contentDescription = "StatsIcon",
                modifier = Modifier.size(16.dp),
                tint = Color.Unspecified
            )

        }


    }
}



