package com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.CommonComposable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EditTextBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.HintColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R
import kotlinx.coroutines.launch

@Composable
fun TextHeightInputComposable(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    inputType: KeyboardType = KeyboardType.Number,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color = EditTextBackground)
            .border(
                width = 1.5.dp,
                color = BorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            readOnly = true,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = WhiteColor,
                fontFamily = FontFamily(Font(R.font.dmsans_semibold))
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = inputType
            ),
            modifier = Modifier
                .fillMaxWidth()
        )


        if (value.isEmpty()) {
            Text(
                text = hint,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    color = HintColor
                )
            )
        }


        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    role = null,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() })
                {
                    onClick()
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeightFieldWithPicker(heights:String ,  onHeightChanged: (String, Int, Int) -> Unit) {
    val height = remember { mutableStateOf("") }
    val coroutinesScope = rememberCoroutineScope()
    val sheetHeightState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showHeightPicker by remember { mutableStateOf(false) }


    TextHeightInputComposable(
        value = heights,
        onValueChange = {},
        hint = "Enter Your Height",
        onClick = {
            showHeightPicker = true
        }
    )

    if (showHeightPicker) {
        ModalBottomSheet(
            onDismissRequest = {
                showHeightPicker = false
            },
            sheetState = sheetHeightState
        ) {

                HeightPickerBottomSheet(
                    initialFeet = 0,
                    onDone = {selectedHeight, feet, inches->
                        height.value = selectedHeight
                        onHeightChanged(selectedHeight, feet, inches)
                        coroutinesScope.launch {
                            sheetHeightState.hide()
                            showHeightPicker = false
                        }
                    }
                )


        }
    }
}
