package com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile

import ImageBottomSheet
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.kickboxing.Activitys.domain.model.CreateProfileRequest
import com.app.kickboxing.Activitys.domain.response.ProfileData
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertState
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.AlertStateCompose
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.ButtonComposable
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.ComposeAlertHost
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.DatePickerComposable
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.DropdownComposableWide
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.RoundedImage
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.TextInputComposable
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.CommonComposable.HeightFieldWithPicker
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.CommonComposable.PunchFieldWithPicker
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.EditProfile.CommonComposable.WeightFieldWithPicker
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.TopbarComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagDark
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.ColorBagLight
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.MyUtils.BASE_URL
import com.app.kickboxing.Activitys.utils.MyUtils.isInternetAvailable
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val userName = remember { mutableStateOf("") }
    val fullName = remember { mutableStateOf("") }
    val DOB = remember { mutableStateOf("") }
    val gender = remember { mutableStateOf("") }
    val displayHeight = remember { mutableStateOf("") }
    val height = remember { mutableStateOf("0") }
    val weightGoal = remember { mutableStateOf("") }
    val punchAverageGoal = remember { mutableStateOf("") }
    val pointsAverageGoal = remember { mutableStateOf("") }
    val attendanceGoal = remember { mutableStateOf("") }
    val weight = remember { mutableStateOf("") }
    var showPicker by remember { mutableStateOf(false) }
    var showHeightPicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetHeightState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedFinalGender = ""
    var selectedFinalDate = ""
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val mViewModel = hiltViewModel<LoginViewModel>()
    val getProfileState by mViewModel.userGetProfileResponseData.collectAsStateWithLifecycle()
    val updateProfileState by mViewModel.userUpdateProfileResponseData.collectAsStateWithLifecycle()
    val updateProfileResult = updateProfileState?.getContentIfNotHandled()
    var profileData = remember { mutableStateOf<ProfileData?>(ProfileData()) }
    var context = LocalContext.current
    val activity = context as? HomesActivity
    val imageUri = remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val currentState by rememberUpdatedState(getProfileState)
    var wasEdited by remember { mutableStateOf(false) }

    val isEdited by remember(
        userName.value, fullName.value, DOB.value, gender.value, height.value,
        weight.value, weightGoal.value, punchAverageGoal.value, pointsAverageGoal.value, attendanceGoal.value, profileData.value
    ) {
        derivedStateOf {
            userName.value.trim() != (profileData.value?.userName?.trim() ?: "") || fullName.value.trim() != (profileData.value?.fullName?.trim() ?: "") || DOB.value.trim() != (profileData.value?.DOB?.trim() ?: "") ||
                    gender.value.trim() != (profileData.value?.gender?.trim() ?: "") || height.value.trim() != (profileData.value?.height?.toString()?.trim() ?: "") ||
                    weight.value.trim() != (profileData.value?.weight?.toString()?.trim() ?: "") || weightGoal.value.trim() != (profileData.value?.weightGoals?.toString()?.trim() ?: "") || punchAverageGoal.value.trim() != (profileData.value?.punchAverageGoal?.toString()?.trim() ?: "") || pointsAverageGoal.value.trim() != (profileData.value?.pointAverageGoal?.toString()?.trim() ?: "") ||
                    attendanceGoal.value.trim() != (profileData.value?.classAttendenceGoal?.toString()?.trim() ?: "")
        }
    }

    LaunchedEffect(currentState) {
        if(!isInternetAvailable(context)){
            activity?.showErrorAlert("Please check your internet connection.")
        }

        if (currentState == null) {
            mViewModel.userGetProfile()
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {

        Column(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ColorBagDark,
                            ColorBagDark,
                            ColorBagLight
                        )
                    )
                )
        ) {

            TopbarComposable(
                text = "Edit Profile",
                onClickBack = { navController.navigateUp() },
                isBackVisible = true
            )


            Spacer(modifier = Modifier.height(10.dp))
            Log.e("profileData", "${profileData.value?.profile}")
            val (feet, inches) = convertCmToFeetInches(if (!height.value.isNullOrEmpty()) height.value.toDouble() else 0.0)
            ProfileFormSection(
                userName = userName.value,
                onUserNameChange = { userName.value = it },
                fullName = fullName.value,
                onFullNameChange = { fullName.value = it },
                dob = DOB.value,
                onDobChange = { DOB.value = it },
                gender = gender.value,
                onGenderChange = { gender.value = it },
                displayHeight = displayHeight.value,
                height = height.value,
                onHeightChange = { displayText, feet, inches ->
                    displayHeight.value = displayText
                    height.value =
                        convertFeetInchesToCm(feet = feet, inches = inches).toInt().toString()
                },
                weight = weight.value,
                onWeightChange = { weight.value = it },
                weightGoal = weightGoal.value,
                onWeightGoalChange = { weightGoal.value = it },
                punchAverageGoal = punchAverageGoal.value,
                onPunchAverageGoalChange = { punchAverageGoal.value = it },
                pointsAverageGoal = pointsAverageGoal.value,
                onPointsAverageGoalChange = { pointsAverageGoal.value = it },
                attendanceGoal = attendanceGoal.value,
                onAttendanceGoalChange = { attendanceGoal.value = it },
                onUpdateProfile = {
                    if (!isInternetAvailable(context)) {
                        activity?.showErrorAlert("Please check your internet connection.")
                        return@ProfileFormSection
                    }

                    if (userName.value.trim().isEmpty() || fullName.value.trim().isEmpty()) {
                        activity?.showErrorAlert("Please fill all required fields.")
                        return@ProfileFormSection
                    }

                    if (userName.value.trim().length < 2) {
                        activity?.showErrorAlert("Username must be at least 2 characters long.")
                        return@ProfileFormSection
                    }

                    if (fullName.value.trim().length < 2) {
                        activity?.showErrorAlert("Full name must be at least 2 characters long.")
                        return@ProfileFormSection
                    }
//                    if (!isEdited) {
//                        activity?.showErrorAlert("Please check your internet connection.")
//                        wasEdited = false
//                        return@ProfileFormSection
//                    }
//                    wasEdited = true

                    mViewModel.userUpdateProfile(
                        CreateProfileRequest(
                            userName = userName.value,
                            fullName = fullName.value,
                            DOB = DOB.value ?: "",
                            gender = gender.value,
                            height = height.value.toDoubleOrNull() ?: 0.0,
                            weight = weight.value.toIntOrNull() ?: 0,
                            userImageUri = if (imageUri.value != "") imageUri.value.toUri() else null,
                            weightGoals = weightGoal.value.toIntOrNull() ?: 0,
                            punchAverageGoal = punchAverageGoal.value.toIntOrNull() ?: 0
                        ),
                        context
                    )
                }
                ,
                keyboardController,
                focusManager,
                profileData.value?.profile.toString(),
                selectedImageUri = { imageUri.value = it },
                imgUri = imageUri.value
            )


        }


        when (val state = getProfileState?.getContentIfNotHandled()) {
            is Results.Success -> {
                Prefs.init().myProfileData = state.data.data
                profileData.value = state.data.data
                //   Log.e("tttddatdatatdatad","{$profileData}")
                val (feet, inches) = convertCmToFeetInches(
                    profileData.value?.height?.toDouble() ?: 0.0
                )
                userName.value = profileData.value?.userName ?: ""
                fullName.value = profileData.value?.fullName ?: ""
                profileData.value?.DOB?.takeIf { it.isNotBlank() }?.let { dobString ->
                    val newDate = convertToMMddyyyy(dobString)
                    if (newDate != null) {
                        DOB.value = newDate
                    }
                }

                gender.value = profileData.value?.gender ?: ""
                height.value = profileData.value?.height.toString()
                displayHeight.value = "${feet}' ${inches}\""
                weight.value = profileData.value?.weight.toString()
                weightGoal.value = profileData.value?.weightGoals.toString()
                punchAverageGoal.value = profileData.value?.punchAverageGoal.toString()
                pointsAverageGoal.value = profileData.value?.pointAverageGoal.toString()
                attendanceGoal.value = profileData.value?.classAttendenceGoal.toString()


            }

            is Results.Failure -> {
                // Handle failure
                Log.e("Profile", "Failed to fetch profile data")
            }

            is Results.Loading -> {
                // Show loading indicator
            }

            null -> {
                // No new event
            }
        }


        LaunchedEffect(updateProfileResult) {
            when (updateProfileResult) {
                is Results.Success -> {
                    val updatedProfile = updateProfileResult.data.data
                    Prefs.init().myProfileData = updatedProfile
                    profileData.value = updatedProfile

                    userName.value = updatedProfile.userName ?: ""
                    fullName.value = updatedProfile.fullName ?: ""
                    DOB.value = updatedProfile.DOB ?: ""
                    gender.value = updatedProfile.gender ?: ""
                    height.value = updatedProfile.height?.toString() ?: ""
                    weight.value = updatedProfile.weight?.toString() ?: ""
                    weightGoal.value = updatedProfile.weightGoals?.toString() ?: ""
                    punchAverageGoal.value = updatedProfile.punchAverageGoal?.toString() ?: ""
                    pointsAverageGoal.value = updatedProfile.pointAverageGoal?.toString() ?: ""
                    attendanceGoal.value = updatedProfile.classAttendenceGoal?.toString() ?: ""

                    activity?.showSuccessAlert("Profile updated successfully.")
                    wasEdited = false
                    navController.navigateUp()
                }

                is Results.Failure -> {
                    activity?.showErrorAlert("Profile update failed.")
                }
                else -> {}
            }
        }

        Log.e("UpdateProfile", "isEdited: $isEdited, wasEdited: $wasEdited before update call")



    }


}

@Composable
fun InputTitle(title: String){
    Column {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontSize = 14.sp,
                color = WhiteColor,
                textAlign = TextAlign.Start
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

    }
}

fun convertToMMddyyyy(input: String): String? {

    val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    originalFormat.timeZone = TimeZone.getTimeZone("UTC")

    val targetFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    return try {
        val date: Date = originalFormat.parse(input) ?: return null
        targetFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileFormSection(
    userName:String,
    onUserNameChange: (String) -> Unit,
    fullName: String,
    onFullNameChange: (String) -> Unit,
    dob: String,
    onDobChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    displayHeight: String,
    height: String,
    onHeightChange: (String, Int, Int) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    weightGoal: String,
    onWeightGoalChange: (String) -> Unit,
    punchAverageGoal: String,
    onPunchAverageGoalChange: (String) -> Unit,
    pointsAverageGoal: String,
    onPointsAverageGoalChange: (String) -> Unit,
    attendanceGoal: String,
    onAttendanceGoalChange: (String) -> Unit,
    onUpdateProfile: () -> Unit,
    keyboardController: SoftwareKeyboardController?,
    focusManager : FocusManager,
    profile: String,
    selectedImageUri: (String) -> Unit,
    imgUri: String? = null
    ) {

    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var imageUri by remember { mutableStateOf(imgUri ?: "") }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, bottom = 50.dp)
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {

                        keyboardController?.hide()
                        focusManager.clearFocus()
                    })
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {


            Spacer(modifier = Modifier.height(40.dp))


            Box(
                modifier = Modifier
                    .size(133.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                RoundedImage(
                    url = if (imageUri != "") imageUri else ("$BASE_URL+ profile.toString()"),
                    size = 133
                )

                Icon(
                    painter = painterResource(id = R.drawable.edit_image),
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(35.dp)
                        .align(Alignment.BottomEnd)
                        .clickable {
                            showSheet = true
                        },
                    tint = Color.Unspecified
                )
                if (showSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showSheet = false },
                        sheetState = sheetState,
                    ) {
                        ImageBottomSheet(
                            onDone = { uri ->
                                Log.d("CameraImage", "Image URI: $uri")
                                imageUri = uri.toString()
                                selectedImageUri(uri.toString())
                                showSheet = false
                            },
                            onCancel = {
                                showSheet = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(38.dp))

            InputTitle(title = "Username")
            TextInputComposable(
                value = userName,
                onValueChange = onUserNameChange,
                hint = "Enter Your Username",
                maxLength = 15
            )

            Spacer(modifier = Modifier.height(20.dp))

            InputTitle(title = "Full Name")
            TextInputComposable(
                value = fullName,
                onValueChange = onFullNameChange,
                hint = "Enter Your Full Name",
                maxLength = 20
            )

            Spacer(modifier = Modifier.height(20.dp))


            InputTitle(title = "Date of Birth")
            DatePickerComposable(
                selectedDate = dob,
                onDateSelected = onDobChange,
                hint = "MM/DD/YYYY",
                restrictCalendar = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            InputTitle(title = "Gender")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()

            ) {
                DropdownComposableWide(
                    selectedOption = gender,
                    modifier = Modifier.fillMaxWidth(),
                    hint = "Select Gender",
                    onOptionSelected = onGenderChange,
                    options = listOf("Male", "Female", "Other")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            InputTitle(title = "Height (Feet)")
            HeightFieldWithPicker(displayHeight, onHeightChange)


            Spacer(modifier = Modifier.height(20.dp))

            InputTitle(title = "Weight (Lb)")
            WeightFieldWithPicker(weight, onWeightChange)

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Set Goals",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                    fontSize = 20.sp,
                    color = WhiteColor,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(25.dp))

            InputTitle(title = "Weight Goals")
            WeightFieldWithPicker(weightGoal, onWeightGoalChange)

            Spacer(modifier = Modifier.height(20.dp))

            InputTitle(title = "Punch Average Goal")
            PunchFieldWithPicker(punchAverageGoal, onPunchAverageGoalChange)


            Spacer(modifier = Modifier.height(20.dp))

//        InputTitle(title = "Points Average Goal")
//        DropdownComposableWide(
//            selectedOption = pointsAverageGoal,
//            modifier = Modifier.fillMaxWidth(),
//            hint = "Set Points Average Goal",
//            onOptionSelected = onPointsAverageGoalChange,
//            options = listOf("")
//        )

            Spacer(modifier = Modifier.height(20.dp))

//        InputTitle(title = "Class Attendance Goal")
//        DropdownComposableWide(
//            selectedOption = attendanceGoal,
//            modifier = Modifier.fillMaxWidth(),
//            hint = "Set Class Attendance Goal",
//            onOptionSelected = onAttendanceGoalChange,
//            options = listOf("")
//        )

            Spacer(modifier = Modifier.height(45.dp))

            ButtonComposable(
                text = "Update Profile",
                onClick = {
                    onUpdateProfile()

                }
            )


            Spacer(modifier = Modifier.height(100.dp))

        }


    }


fun convertFeetInchesToCm(feet: Int, inches: Int): Double {
    return feet * 30.48 + inches * 2.54
}

fun convertCmToFeetInches(cm: Double): Pair<Int, Int> {
    val totalInches = cm / 2.54
    val roundedInches = Math.round(totalInches).toInt()
    val feet = roundedInches / 12
    val inches = roundedInches % 12
    return Pair(feet, inches)
}

@Preview(name = "Phone", device = "spec:width=411dp,height=1530dp", showSystemUi = true)
@Composable
fun EditProfilePreview(){
    EditProfileScreen(rememberNavController())
}