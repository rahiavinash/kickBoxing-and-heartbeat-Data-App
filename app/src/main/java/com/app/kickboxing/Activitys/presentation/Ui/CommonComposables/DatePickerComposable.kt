package com.app.kickboxing.Activitys.presentation.Ui.CommonComposables

import android.app.DatePickerDialog
import android.view.ContextThemeWrapper
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EditTextBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.HintColor
import com.app.kickboxing.R
import java.util.Calendar
import androidx.compose.ui.tooling.preview.Preview
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DatePickerComposable(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(color = EditTextBackground)
        .border(width = 1.5.dp, color = BorderColor, shape = RoundedCornerShape(16.dp)),
    restrictCalendar : Boolean? = null
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

// Set max date to 18 years ago
    val eighteenYearsAgo = Calendar.getInstance().apply {
        add(Calendar.YEAR, -3)
    }
    val themedContext = ContextThemeWrapper(context, R.style.CustomDatePickerDialogTheme)

    val datePickerDialog = remember {
        DatePickerDialog(
            themedContext,
            { _: DatePicker, y: Int, m: Int, d: Int ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, y)
                    set(Calendar.MONTH, m)
                    set(Calendar.DAY_OF_MONTH, d)
                }
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val dateStr = sdf.format(calendar.time)
                onDateSelected(dateStr)
            },
            year - 3,
            month,
            day
        ).apply {
            if (restrictCalendar == true || restrictCalendar == null){
                datePicker.maxDate = eighteenYearsAgo.timeInMillis

            }

        }
    }



    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        placeholder = {
            Text(
                text = hint,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 16.sp,
                    color = HintColor
                )
            )
        },
        singleLine = true,
        enabled = false,
        modifier = modifier.clickable { datePickerDialog.show() },
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = "Calendar Icon",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        },
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = WhiteColor,
            fontFamily = FontFamily(Font(R.font.dmsans_semibold))
        )
    )
}

@Preview
@Composable
fun DatePickerPreview(){
    DatePickerComposable(
        selectedDate = "",
        onDateSelected = {},
        hint = "MM/DD/YYYY"
    )
}

