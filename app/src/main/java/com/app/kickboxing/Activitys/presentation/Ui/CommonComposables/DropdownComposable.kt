package com.app.kickboxing.Activitys.presentation.Ui.CommonComposables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackCardBackground
import com.app.kickboxing.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuComposable(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,

) {
    var expanded by remember { mutableStateOf(false) }

    val borderColor = Color(0x669F9F9F)
    val shape = RoundedCornerShape(16.dp)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }, // Handles toggle
        modifier = Modifier.wrapContentWidth()
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .border(width = 1.5.dp, color = borderColor, shape = shape)
                .background(Color.Transparent)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .menuAnchor() // Anchor for dropdown menu
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = selectedOption.ifBlank { label },
                    color = Color.White,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_dropdown_arrow),
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (expanded) 180f else 0f)
                )
            }
        }

        Box(  modifier = Modifier
            .padding(top = 45.dp , end = 50.dp)){
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(BlackCardBackground)
                    .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(5.dp))
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    Column {
                        options.forEach { selectionOption ->
                            DropdownMenuItem(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = {
                                    Text(
                                        text = selectionOption,
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontFamily = FontFamily(Font(R.font.dmsans_semibold))
                                        )
                                    )
                                },
                                onClick = {
                                    onOptionSelected(selectionOption)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}


@Preview
@Composable
fun DropdownPreview() {
    DropdownMenuComposable(
        label = "Dropdown",
        options = listOf("Option 1", "Option 2", "Option 3", "Option 4"),
        selectedOption = "Option 1",
        onOptionSelected = {}
    )
}
