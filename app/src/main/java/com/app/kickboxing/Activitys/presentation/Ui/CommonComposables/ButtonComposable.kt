package com.app.kickboxing.Activitys.presentation.Ui.CommonComposables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.R

@Composable
fun ButtonComposable(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 10.dp
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ButtonRed),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(
                enabled = true,
                onClickLabel = "",
                onClick = {
                },
                role = null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )

    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = verticalPadding),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontSize = 16.sp,
                color = WhiteColor
            )
        )
    }
}

@Preview
@Composable
fun ButtonPreview(){
    ButtonComposable(text = "Button Text", onClick = {})
}