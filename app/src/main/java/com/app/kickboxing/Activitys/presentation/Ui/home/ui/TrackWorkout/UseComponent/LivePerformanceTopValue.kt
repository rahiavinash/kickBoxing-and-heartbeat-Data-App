package com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.UseComponent

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.R
@Composable
fun LivePerformance(heartbeat:String, power:String , punches:String ,  punchPerMinutes: String, koPoints : String) {
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
            //  .padding( end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hearrate),
                    contentDescription = "StatsIcon",
                    modifier = Modifier.size(22.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "Maximum Heart Rate",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                        fontSize = 15.sp,
                        color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                    )
                )   
            }
           
            Text(
                text = heartbeat,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                    fontSize = 15.sp,
                    color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                )
            )

            
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            //  .padding( end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_power_new),
                    contentDescription = "StatsIcon",
                    modifier = Modifier.size(22.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "Maximum Power",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                        fontSize = 15.sp,
                        color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                    )
                )
            }

            Text(
                text = power,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                    fontSize = 15.sp,
                    color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                )
            )


        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            //  .padding( end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_new_flame),
                    contentDescription = "StatsIcon",
                    modifier = Modifier.size(22.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "Total Punches",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                        fontSize = 15.sp,
                        color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                    )
                )
            }

            Text(
                text = punches,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                    fontSize = 15.sp,
                    color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                )
            )


        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            //  .padding( end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_workout_density),
                    contentDescription = "StatsIcon",
                    modifier = Modifier.size(22.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "Punches Per Minutes",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                        fontSize = 15.sp,
                        color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                    )
                )
            }

            Text(
                text = punchPerMinutes,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                    fontSize = 15.sp,
                    color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))


        Row(
                modifier = Modifier
                    .fillMaxWidth(),
                //  .padding( end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_workout_density),
                        contentDescription = "StatsIcon",
                        modifier = Modifier.size(22.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = "KO Points",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                            fontSize = 15.sp,
                            color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                        )
                    )
                }

                Text(
                    text = koPoints,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                        fontSize = 15.sp,
                        color = colorResource(id = com.hb.dialog.R.color.dialog_white)
                    )
                )


            }




    }
}