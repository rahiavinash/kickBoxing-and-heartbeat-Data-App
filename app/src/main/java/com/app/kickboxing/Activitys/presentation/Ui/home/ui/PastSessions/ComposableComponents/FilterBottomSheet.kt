package com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.ComposableComponents

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.DatePickerComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.PastSessions.PastSessionsDataModel.FilterType
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BorderColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EditTextBackground
import com.app.kickboxing.Activitys.utils.MyUtils.convertToUTC
import com.app.kickboxing.R
import java.util.logging.Filter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FilterBottomSheet(
    onDone: (FilterType?, FilterDates?) -> Unit,
    onClose: () -> Unit,
    filterType: FilterType? = null,
    filterDates: FilterDates? = null,
    showSelectDateOption: Boolean
) {
    var showCustomDates by remember { mutableStateOf(filterType == FilterType.SELECT_DATE) }
    var startDate by remember { mutableStateOf(filterDates?.startDate ?: "") }
    var endDate by remember { mutableStateOf(filterDates?.endDate ?: "") }
    var startDateUTC by remember { mutableStateOf("") }
    var endDateUTC by remember { mutableStateOf("") }
    var currentlySelectedFilter by rememberSaveable { mutableStateOf(filterType) }
    var selectedFilter by rememberSaveable  {
        mutableStateOf (
            when (filterType) {
                FilterType.WEEKLY -> "Weekly"
                FilterType.MONTHLY -> "Monthly"
                FilterType.YEARLY -> "Yearly"
                FilterType.SELECT_DATE -> "Custom"
                else -> ""
            }
        )
    }

    val filters = listOf("Weekly", "Monthly", "Yearly")
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Close",
                modifier = Modifier
                    .size(35.dp)
                    .padding(5.dp)
                    .clickable { onClose() },
                tint = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Filter",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.padding(5.dp)
            )

            Text(
                text = "Reset",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .padding(5.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }

                    ) {
                        selectedFilter = ""
                        currentlySelectedFilter = null
                        startDate = ""
                        endDate = ""
                        startDateUTC = ""
                        endDateUTC = ""
                        showCustomDates = false
                       onDone(currentlySelectedFilter,FilterDates())
                    }
            )
        }

        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(3.dp))


        filters.forEachIndexed { index, filter ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        selectedFilter = filter
                        showCustomDates = false
                        currentlySelectedFilter = when (filter) {
                            "Weekly" -> FilterType.WEEKLY
                            "Monthly" -> FilterType.MONTHLY
                            "Yearly" -> FilterType.YEARLY
                            else -> null
                        }
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = filter,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(
                        color = if (selectedFilter == filter) Color.Red else MaterialTheme.colorScheme.onSurface

                    )
                )
            }

            val isLastItem = index == filters.lastIndex
            val shouldSkipDivider = isLastItem && !showSelectDateOption

            if (!shouldSkipDivider) {
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )
            }
        }


        if(showSelectDateOption) {


        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        selectedFilter = "Custom"
                        currentlySelectedFilter = FilterType.SELECT_DATE
                        showCustomDates = !showCustomDates
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Date",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(
                        color = if (selectedFilter == "Custom") Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                )

                Icon(
                    painter = painterResource(R.drawable.ic_dropdown_arrow),
                    contentDescription = "down arrow",
                    modifier = Modifier
                        .size(35.dp),
                    tint = if (selectedFilter == "Custom") Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }

            if (showCustomDates) {
                Spacer(modifier = Modifier.height(10.dp))

                DatePickerComposable(
                    selectedDate = startDate,
                    onDateSelected = {
                        startDate = it
                        startDateUTC = convertToUTC(it) ?: ""
                    },
                    hint = "Start Date",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(color = EditTextBackground)
                        .border(
                            width = 1.5.dp,
                            color = BorderColor,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    restrictCalendar = false

                )

                Spacer(modifier = Modifier.height(8.dp))

                DatePickerComposable(
                    selectedDate = endDate,
                    onDateSelected = {
                        endDate = it
                        endDateUTC = convertToUTC(it) ?: ""
                    },
                    hint = "End Date",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(color = EditTextBackground)
                        .border(
                            width = 1.5.dp,
                            color = BorderColor,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    restrictCalendar = false
                )
            }
        }
    }

        Spacer(modifier = Modifier.height(20.dp))


        Button(
            onClick = {
                val dates = if (currentlySelectedFilter == FilterType.SELECT_DATE) {
                    FilterDates(startDate, startDateUTC, endDate, endDateUTC)
                } else {
                    null
                }
                onDone(currentlySelectedFilter, dates)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xE2FF0000))
        ) {
            Text (
                "Apply",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}



@Preview
@Composable
fun FilterPreview() {

}

data class FilterDates(
    var startDate: String? = null,
    var startDateUTC: String? = null,
    var endDate: String? = null,
    var endDateUTC: String? = null
)


