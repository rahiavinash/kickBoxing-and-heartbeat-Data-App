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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R

@Composable
fun SessionItem(
    date: String,
    workoutTime: String,
    workoutDuration: String,
    points: Int,
    punches: Int,
    power: Int,
    punches_min: Int,
    heartRate: Int,
    onClick : () -> Unit
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
            .padding(horizontal =  12.dp , vertical = 18.dp)
            .clickable(
                enabled = true,
                onClickLabel = "",
                onClick = { onClick()
                },
                role = null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),

    ) {



        Row(modifier = Modifier.fillMaxWidth()) {

            Icon(
                painter = painterResource(id = R.drawable.ic_calender),
                modifier = Modifier
                    .size(20.dp)
                    .width(IntrinsicSize.Min),
                contentDescription = "Calender",
                tint = ButtonRed
            )
            Spacer(modifier = Modifier.width(14.dp))


            Text(
                text = "Date",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = date,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {

            Icon(
                painter = painterResource(id = R.drawable.ic_clock),
                modifier = Modifier
                    .size(20.dp)
                    .width(IntrinsicSize.Min),
                contentDescription = "Clock",
                tint = ButtonRed
            )
            Spacer(modifier = Modifier.width(14.dp))


            Text(
                text = "Workout Time",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = workoutTime,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {

            Icon(
                painter = painterResource(id = R.drawable.ic_clock),
                modifier = Modifier
                    .size(20.dp)
                    .width(IntrinsicSize.Min),
                contentDescription = "Clock",
                tint = ButtonRed
            )
            Spacer(modifier = Modifier.width(14.dp))


            Text(
                text = "Workout Duration",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = workoutDuration,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                    color = WhiteColor
                )
            )

        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "Points",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                        color = WhiteColor
                    )
                )

                Text(
                    text = points.toString(),
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        color = WhiteColor
                    )
                )

            }

           // Spacer(modifier = Modifier.weight(2f))

            //   Spacer(modifier = Modifier.width(15.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "Punches",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                        color = WhiteColor
                    )
                )

                Text(
                    text = punches.toString(),
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        color = WhiteColor
                    )
                )
            }

            //Spacer(modifier = Modifier.weight(2f))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "Power",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                        color = WhiteColor
                    )
                )

                Text(
                    text = power.toString(),
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        color = WhiteColor
                    )
                )
            }

           // Spacer(modifier = Modifier.weight(2f))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "Punches/min",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                        color = WhiteColor
                    )
                )

                Text(
                    text = punches_min.toString(),
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        color = WhiteColor
                    )
                )
            }

           // Spacer(modifier = Modifier.weight(2f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Heart Rate",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.dmsans_regular)),
                        color = WhiteColor
                    )
                )

                Text(
                    text = heartRate.toString(),
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        color = WhiteColor
                    )
                )
            }




        }
    }
}


@Preview
@Composable
fun SessionItemPreview() {
    SessionItem("22/04/2025", "02:20 PM", "45 minutes", 252, 21, 722, 40, 136,{})
}