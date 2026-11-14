package com.app.kickboxing.Activitys.presentation.Ui.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopbarComposable(text: String,   onClickBack: (() -> Unit)? = null, isBackVisible: Boolean = false) {
    Box() {
        Column {

            Spacer(modifier = Modifier.height(51.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    modifier = Modifier,
                    color = colorResource(id = com.hb.dialog.R.color.dialog_white),
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                        fontSize = 20.sp
                    )
                )
            }
        }

        if (isBackVisible){
            Column(
                modifier = Modifier.padding(start = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(39.dp))

                Icon(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Performance Icon",
                    modifier = Modifier.size(50.dp)
                        .clickable(
                            enabled = true,
                            onClickLabel = "",
                            onClick = {
                                onClickBack?.invoke()
                            },
                            role = null,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

    }

}

@Preview
@Composable
fun TopBarPreview(){
    TopbarComposable(text = "Notification", onClickBack = {}, isBackVisible = true)
}