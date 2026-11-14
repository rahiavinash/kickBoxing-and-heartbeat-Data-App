package com.app.kickboxing.Activitys.presentation.Ui.home.ui.Settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.HintColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R

@Composable
fun UserGoals(weightGoal : Int,punchGoal : Int, pointsGoal : Int, attendanceGoal : Int ){


    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {


            Text(
                text = "Weight Goal",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 16.sp,
                    color = HintColor
                )
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$weightGoal lb" ,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                    color = WhiteColor
                )
            )

        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Punch Average Goal",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                    color = HintColor
                )
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = punchGoal.toString(),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                    color = WhiteColor
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

//        Row(
//            modifier = Modifier,
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            Text(
//                text = "Point Average Goal",
//                style = TextStyle(
//                    fontSize = 16.sp,
//                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
//                    color = HintColor
//                )
//            )
//
//            Spacer(modifier = Modifier.weight(1f))
//            Text(
//                text = pointsGoal.toString(),
//                style = TextStyle(
//                    fontSize = 16.sp,
//                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
//                    color = WhiteColor
//                )
//            )
//        }

      //  Spacer(modifier = Modifier.height(10.dp))

//        Row(
//            modifier = Modifier,
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            Text(
//                text = "Class Attendance Goal",
//                style = TextStyle(
//                    fontSize = 16.sp,
//                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
//                    color = HintColor
//                )
//            )
//
//            Spacer(modifier = Modifier.weight(1f))
//            Text(
//                text = attendanceGoal.toString(),
//                style = TextStyle(
//                    fontSize = 16.sp,
//                    fontFamily = FontFamily(Font(R.font.dmsans_medium)),
//                    color = WhiteColor
//                )
//            )
//        }
        }
}

@Preview
@Composable
fun UserGoalsPreview() {

    UserGoals(250,35,2000,45)
}