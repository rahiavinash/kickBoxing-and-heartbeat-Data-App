package com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.CommonComposable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.widget.EditText
import android.widget.NumberPicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ButtonRed
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.Gray
import com.app.kickboxing.R


@SuppressLint("NewApi")
@Composable
fun HeightPickerBottomSheet(
    initialFeet: Int = 0,
    initialInches: Int = 0,
    onDone: (String, Int, Int) -> Unit
) {
    val feetRange = (1..8).toList()
    val inchRange = (0..11).toList()

    val safeInitialFeet = if (initialFeet in feetRange) initialFeet else 1
    val safeInitialInches = if (initialInches in inchRange) initialInches else 0

    var selectedFeetIndex by remember {
        mutableStateOf(feetRange.indexOf(safeInitialFeet))
    }
    var selectedInchIndex by remember {
        mutableStateOf(inchRange.indexOf(safeInitialInches))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Height",
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            style = TextStyle(color = BlackColor)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Feet picker
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = "Feet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(color = Gray)
                )
                Spacer(modifier = Modifier.height(10.dp))

                AndroidView(
                    factory = { context ->
                        NumberPicker(context).apply {
                            minValue = 0
                            maxValue = feetRange.lastIndex
                            value = selectedFeetIndex
                            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                            isFocusable = false
                            isFocusableInTouchMode = false
                            isClickable = false
                            displayedValues = feetRange.map { it.toString() }.toTypedArray()
                            wrapSelectorWheel = false
                            textColor = resources.getColor(R.color.colorBagDark)

                            setOnValueChangedListener { _, _, newVal ->
                                selectedFeetIndex = newVal
                            }
                        }
                    },
                    update = { it.value = selectedFeetIndex },
                    modifier = Modifier
                        .width(80.dp)
                        .height(130.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Inches",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(color = Gray)
                )
                AndroidView(
                    factory = { context ->
                        NumberPicker(context).apply {
                            minValue = 0
                            maxValue = inchRange.lastIndex
                            value = selectedInchIndex
                            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                            isFocusable = false
                            isFocusableInTouchMode = false
                            isClickable = false
                            displayedValues = inchRange.map { it.toString() }.toTypedArray()
                            wrapSelectorWheel = false
                            textColor = resources.getColor(R.color.colorBagDark)
                            setOnValueChangedListener { _, _, newVal ->
                                selectedInchIndex = newVal
                            }
                        }
                    },
                    update = { it.value = selectedInchIndex },
                    modifier = Modifier
                        .width(80.dp)
                        .height(150.dp)
                )
            }
        }

        Button(
            onClick = {
                val feet = feetRange.getOrNull(selectedFeetIndex) ?: feetRange.first()
                val inches = inchRange.getOrNull(selectedInchIndex) ?: inchRange.first()
                val height = "$feet' $inches\""
                onDone(height, feet, inches)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xE2FF0000))
        ) {
            Text("Done", color = Color.White)
        }
    }
}


