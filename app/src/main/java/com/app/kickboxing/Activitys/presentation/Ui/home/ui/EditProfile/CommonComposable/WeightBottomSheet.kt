package com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.CommonComposable

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.Gray
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R

@Composable
fun WeightPickerBottomSheet(
    onDone: (Int) -> Unit,
    initialWeight: Int = 100
) {

    val weightRange = (30..400).toList()
    var selectedIndex by remember {
        mutableStateOf(weightRange.indexOf(initialWeight))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Weight",
            fontSize = 25.sp,
            style = TextStyle(
                color = BlackColor
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Lb",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 30.dp),
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(
                        color = BlackColor
                    )
                )

                AndroidView(
                    factory = { context ->
                        NumberPicker(context).apply {
                            minValue = 0
                            maxValue = weightRange.size - 1
                            value = selectedIndex
                            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                            isFocusable = false
                            isFocusableInTouchMode = false
                            isClickable = false
                            displayedValues = weightRange.map { it.toString() }.toTypedArray()
                            wrapSelectorWheel = false
                            textColor = resources.getColor(R.color.colorBagDark)
                            setOnValueChangedListener { _, _, newVal ->
                                selectedIndex = newVal
                            }
                        }
                    },
                    update = {  it.value = selectedIndex },

                    modifier = Modifier
                        .width(100.dp)
                        .height(130.dp)


                )

            }
        }

        Button(
            onClick = { onDone(weightRange[selectedIndex]) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xE2FF0000))

            ) {
            Text(text = "Done", color = WhiteColor)
        }
    }
}

@Composable
fun ShowWeightPicker() {
    var weight by remember { mutableStateOf(30) }
    var showSheet by remember { mutableStateOf(true) }

    if (showSheet) {
        WeightPickerBottomSheet(
            initialWeight = weight,
            onDone = {
                weight = it
                showSheet = false
            }
        )
    } else {
        Text("Selected weight: $weight lbs")
    }
}


@Preview()
@Composable
fun WeightPreview(){
    ShowWeightPicker()
}

