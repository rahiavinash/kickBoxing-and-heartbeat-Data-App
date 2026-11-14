package com.app.kickboxing.Activitys.presentation.Ui.CommonComposables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EditTextBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.HintColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R

@Composable
fun TextInputComposable(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(color = EditTextBackground)
        .border(
            width = 1.5.dp,
            color = BorderColor,
            shape = RoundedCornerShape(16.dp)
        )
        .padding(horizontal = 16.dp, vertical = 20.dp),
    inputType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLength: Int = 50
) {

    Box(modifier = modifier) {
        BasicTextField(
            value = value.take(maxLength),
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChange(it)
                } },
            singleLine = singleLine,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = WhiteColor,
                fontFamily = FontFamily(Font(R.font.dmsans_semibold))
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = inputType,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()

        )

        if (value.isEmpty() ) {
            Text(
                text = hint,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 16.sp,
                    color = HintColor
                ),
                modifier = Modifier
                    .padding(start = 0.dp)
            )
        }
    }
}



@Preview
@Composable
fun TextInputPreview(){
    TextInputComposable(
        value = "",
        onValueChange = {},
        hint = "User Name"
    )
}
