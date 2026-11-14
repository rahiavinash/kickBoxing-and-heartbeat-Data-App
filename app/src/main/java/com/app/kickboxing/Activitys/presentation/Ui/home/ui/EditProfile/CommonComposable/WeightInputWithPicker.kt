package com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.CommonComposable

import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EditTextBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.HintColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch


@Composable
fun TextPunchInputComposable(
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
                    interactionSource = remember { MutableInteractionSource() }) {
                    onClick()

                }
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightFieldWithPicker(weightFromApi:String , onWeightChange:(String)->Unit) {
    val weight = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPicker by remember { mutableStateOf(false) }

    val weightInLbs = weightFromApi.toIntOrNull()
    val displayValue = if (weightInLbs != null && weightInLbs > 0) "$weightInLbs lb" else ""

    TextPunchInputComposable(
        value = displayValue,
        onValueChange = {},
        hint = "Enter Your Weight",
        onClick = {
            showPicker = true
        }
    )

    if (showPicker) {

        ModalBottomSheet(
            onDismissRequest = { showPicker = false },
            sheetState = sheetState
        ) {
            Log.e("avfdg","${weight.value}")

            WeightPickerBottomSheet(

                initialWeight = weight.value.toIntOrNull() ?:100 ,
                onDone = {
                    weight.value = it.toString()
                    onWeightChange(it.toString())
                    coroutineScope.launch {
                        sheetState.hide()
                        showPicker = false
                    }
                }
            )
        }


    }
}



@Preview
@Composable
fun WeightInputPreview(){
  //  WeightFieldWithPicker()
}






