package com.app.kickboxing.Activitys.presentation.Ui.home.ui.Leaderboard.ComposableComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.RoundedImage
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.TextOffWhite
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R

@Composable
fun LeaderboardComposableComponents(rank: Int, imageUrl: String, userName: String, score: Int) {
    Box(modifier = Modifier.padding(top = 20.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    color = BlackCardBackground
                    )
                .border(
                    width = 1.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(28.dp)
                )
                .height(IntrinsicSize.Min)
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                RankComposable(rank = rank)

                Spacer(modifier = Modifier.width(10.dp))

                    RoundedImage(
                        url = if (imageUrl.isNullOrBlank()) "https://ctmirror.org/wp-content/uploads/2021/01/dummy-man-570x570-1.png" else imageUrl,
                        size = 38
                    )


                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = userName,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                        fontSize = 15.sp,
                        color = WhiteColor,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 5.dp),
                    maxLines = 2
                )

                Text(
                    text = "$score",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                        fontSize = 16.sp,
                        color = WhiteColor
                    ),
                    maxLines = 1
                )

            }

        }
    }
}

@Composable
fun RankComposable(rank: Int) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = BlackColor)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            text = String.format("%02d", rank),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontSize = 20.sp,
                color = WhiteColor,
                platformStyle = PlatformTextStyle(includeFontPadding = false)

            )
        )

        Text(
            modifier = Modifier,
            text = "Rank",
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontSize = 20.sp,
                color = WhiteColor,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
        )
    }
}


@Preview
@Composable
fun MyPointsPreview() {
    LeaderboardComposableComponents(
        rank = 3,
        imageUrl = "https://ctmirror.org/wp-content/uploads/2021/01/dummy-man-570x570-1.png",
        userName = "James Philips",
        score = 8400
    )
}