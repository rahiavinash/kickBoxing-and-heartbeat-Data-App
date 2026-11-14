package com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.TextOffWhite
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R

@Composable
fun MyPointsComposable(points: Int){
    Box(modifier = Modifier.padding(horizontal = 25.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    BlackCardBackground
//                    brush = Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0x4D636363),
//                            Color(0xFF636363),
//                            Color(0xFF636363)
//                        )
//                    )
                )
                .border(
                    width = 1.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .height(IntrinsicSize.Min)
                .padding(vertical = 20.dp, horizontal = 25.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_trophy),
                    contentDescription = "TrophyIcon",
                    modifier = Modifier.size(61.dp),
                    tint = Color.Unspecified
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Your Total Points",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                            fontSize = 20.sp,
                            color = TextOffWhite
                        )
                    )

                    Text(
                        modifier = Modifier,
                        text = "$points Pts",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                            fontSize = 30.sp,
                            color = WhiteColor
                        )
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun MyPointsPreview(){
    MyPointsComposable(points = 840)
}