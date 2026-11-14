package com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.CommonComposable

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.Gray
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R
import kotlinx.coroutines.launch

@Composable
fun PunchBottomSheet(
    onDone: (Int) -> Unit,
    initialPunch: Int = 75
) {

    val punchRange = (15..400).toList()
    var selectedIndex by remember {
        mutableStateOf(punchRange.indexOf(initialPunch))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Punch Average Goal",
            fontSize = 25.sp,
            style = TextStyle(
                color = Gray
            ),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {



            AndroidView(
                factory = { context ->
                    NumberPicker(context).apply {
                        minValue = 0
                        maxValue = punchRange.size - 1
                        value = selectedIndex
                        displayedValues = punchRange.map { it.toString() }.toTypedArray()
                        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                        isFocusable = false
                        isFocusableInTouchMode = false
                        isClickable = false
                        wrapSelectorWheel = false
                        textColor = resources.getColor(R.color.colorBagDark)
                        setOnValueChangedListener { _, _, newVal ->
                            selectedIndex = newVal
                        }
                    }
                },
                update = { it.value = selectedIndex },

                modifier = Modifier
                    .width(100.dp)
                    .height(130.dp)

            )

        }


        Button(
            onClick = { onDone(punchRange[selectedIndex]) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xE2FF0000)),

            ) {
            Text(text = "Done", color = WhiteColor)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PunchFieldWithPicker(punchFromApi:String , onPunchChange:(String)->Unit) {
    val punch = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPicker by remember { mutableStateOf(false) }

    val punches = punchFromApi.toIntOrNull()?: 0
    val displayValue = if (punches != null && punches > 0) "$punches" else ""

    TextPunchInputComposable(
        value = displayValue,
        onValueChange = {},
        hint = "Set Your Punch Goal",
        onClick = {
            showPicker = true
        }
    )

    if (showPicker) {

        ModalBottomSheet(
            onDismissRequest = { showPicker = false },
            sheetState = sheetState
        ) {
            Log.e("avfdg","${punch.value}")

            PunchBottomSheet(

                initialPunch = punch.value.toIntOrNull() ?:100 ,
                onDone = {
                    punch.value = it.toString()
                    onPunchChange(it.toString())
                    coroutineScope.launch {
                        sheetState.hide()
                        showPicker = false
                    }
                }
            )
        }


    }
}





