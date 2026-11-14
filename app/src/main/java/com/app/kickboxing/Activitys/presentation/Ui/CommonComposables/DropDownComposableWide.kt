package com.app.kickboxing.Activitys.presentation.Ui.CommonComposables

import android.util.Log
import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EditTextBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.HintColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownComposableWide(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    hint: String?=null,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(58.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(EditTextBackground)
        .border(1.5.dp, BorderColor, RoundedCornerShape(16.dp)),
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    var currentSelection by rememberSaveable  { mutableStateOf(selectedOption) }
    Log.d("tttddatdatatdatad", selectedOption.toString())
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .height(58.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(EditTextBackground)
                .border(1.5.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(start = 16.dp, end = 16.dp, top = 17.dp, bottom = 15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text =  if (currentSelection.isNotBlank()) {
                        currentSelection
                    } else if (selectedOption.isNotEmpty() && currentSelection.isEmpty()) {
                        selectedOption
                    } else {
                        hint ?: ""
                    },
                    color = if (currentSelection.isNotBlank()) {
                        WhiteColor
                    }
                   else if (selectedOption.isNotEmpty() && currentSelection.isEmpty()) {
                        WhiteColor
                    }else{
                        HintColor
                    },
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_dropdown_arrow),
                    contentDescription = "Dropdown Arrow",
                    tint = HintColor,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (expanded) 180f else 0f)
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.dmsans_medium)),
                            color = BlackColor
                        )
                    },
                    onClick = {
                        currentSelection = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview
@Composable
fun DropdownComposable(){
    DropdownComposableWide(
        selectedOption = "",
        hint = "Select Gender",
        onOptionSelected = {},
        options = listOf("Male", "Female", "Other"),
        modifier = Modifier
    )
}