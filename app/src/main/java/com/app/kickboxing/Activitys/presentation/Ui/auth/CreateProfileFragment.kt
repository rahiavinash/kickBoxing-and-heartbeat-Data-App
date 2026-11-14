package com.app.kickboxing.Activitys.presentation.Ui.auth

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.addCallback
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.kickboxing.Activitys.domain.model.CreateProfileRequest
import com.app.kickboxing.Activitys.presentation.Authentication.ImageSheet
import com.app.kickboxing.Activitys.presentation.Authentication.OnImageSelectedListener
import com.app.kickboxing.Activitys.presentation.Ui.Activities.AuthActivity
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.DatePickerComposable
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.DropdownComposableWide
import com.app.kickboxing.Activitys.presentation.Ui.HomesActivity
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.presentation.Ui.auth.bottomsheets.HeightPickerBottomSheet
import com.app.kickboxing.Activitys.presentation.Ui.auth.bottomsheets.WeightPickerBottomSheet
import com.app.kickboxing.Activitys.presentation.state.Results
import com.app.kickboxing.Activitys.utils.AlertManager
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.R
import com.app.kickboxing.databinding.FragmentCreateProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import ImageBottomSheet as ImageBottomSheet

@AndroidEntryPoint
class CreateProfileFragment : Fragment(), HeightPickerBottomSheet.HeightPickerListener, WeightPickerBottomSheet.WeightPickerListener, OnImageSelectedListener {

    private lateinit var binding: FragmentCreateProfileBinding
    private val IMAGE_PICK_CODE = 1001

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val GALLERY_PERMISSION_REQUEST_CODE = 101
    private var authActivity: AuthActivity? = null
    private var selectedFinalGender = ""
    private var selectedFinalDate = ""
    private val mViewModel by viewModels<LoginViewModel>()
    private var imageUri: Uri? = null
    private var selectedWeightValue: Int? = null
    private var selectedHeightValue: Double? = null
    private val CAMERA_REQUEST_CODE = 102
    private val GALLERY_REQUEST_CODE = 103
    private var cameraImageUri: Uri? = null
    private var tempCameraFile: File? = null


    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                binding.imgProfile.setImageURI(uri)
                imageUri = uri
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AuthActivity) {
            authActivity = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_create_profile,
            container,
            false
        )
        observer()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTouchListenerToHideKeyboard(view)
        binding.editImage.setOnClickListener {

            showImageBottomSheet()
            //  pickImageLauncher.launch("image/*")
        }


        binding.apply {

            usernameEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
            fullNameEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
            fullNameEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
            fullNameEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    datePickerCompose.setContent {
                        val selectedDate = remember { mutableStateOf("") }

                        DatePickerComposable(
                            selectedDate = selectedDate.value,
                            onDateSelected = { newDate ->
                                selectedDate.value = newDate

                            },
                            hint = "MM/DD/YYYY"
                        )

                        selectedFinalDate = selectedDate.value
                    }
                    true
                } else {
                    false
                }
            }

            datePickerCompose.setContent {
                val selectedDate = remember { mutableStateOf("") }
                DatePickerComposable(
                    selectedDate = selectedDate.value,
                    onDateSelected = { newDate ->
                        selectedDate.value = newDate
                    },
                    hint = "MM/DD/YYYY"
                )
                selectedFinalDate = selectedDate.value
            }


            composeGenderDropDown.setContent {
                val selectedGender = remember { mutableStateOf("") }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()

                ) {
                    DropdownComposableWide(
                        selectedOption = selectedGender.value,
                        modifier = Modifier.fillMaxWidth(),
                        hint = "Select Gender",
                        onOptionSelected = { gender ->
                            selectedGender.value = gender
                        },
                        options = listOf("Male", "Female", "Other")
                    )
                }

                selectedFinalGender = selectedGender.value
            }

            heightEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

            heightEditText.setOnClickListener {
                showHeightBottomSheet()
            }

            weightEditText.setOnClickListener {
                showWeightBottomSheet()


            }

            ivBack.setOnClickListener {
                Prefs.init().accessToken = ""
                findNavController().navigateUp()
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                Prefs.init().accessToken = ""
                findNavController().navigateUp()
            }

            btSubmit.setOnClickListener {
                validateProfileForm()
            }
        }
    }

    private fun showWeightBottomSheet() {
        val heightBottomSheet = WeightPickerBottomSheet(this)
        heightBottomSheet.show(parentFragmentManager, heightBottomSheet.tag)
    }

    private fun showImageBottomSheet() {
        val imageBottomSheet = ImageSheet(this@CreateProfileFragment)
        imageBottomSheet.show(parentFragmentManager, imageBottomSheet.tag)
    }

    private fun showHeightBottomSheet() {
        val heightBottomSheet = HeightPickerBottomSheet(this)
        heightBottomSheet.show(parentFragmentManager, heightBottomSheet.tag)
    }


    private fun observer() {
        lifecycleScope.launchWhenStarted {
            mViewModel.userCreateProfileResponseData.collect { result ->
                when (result) {
                    is Results.Loading -> {
                        Log.d("LoginObserver", "Loading...")
                    }

                    is Results.Success -> {
                        if (result.data.success) {
                            (activity as AuthActivity).showSuccessAlert(result.data.message)

                            Prefs.init().isProfileCompleted = true
                            val intent = Intent(requireActivity(), HomesActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            Prefs.init().isProfileCompleted = false
                            result.data.message?.let { authActivity?.showErrorAlert(it) }
                            Log.e("LoginObserver", "API Error: ${result.data.message}")
                        }
                    }

                    is Results.Failure -> {
                        Log.e("LoginObserver", "Failure: ${result.message} (Code: ${result.code})")
                        // Show error message in UI (e.g., Toast)
                        authActivity?.showErrorAlert(result.message)
                    }

                    null -> {
                        Log.d("LoginObserver", "No result")
                    }
                }
            }
        }

    }

    private fun validateProfileForm() {
        val username = binding.usernameEditText.text?.toString()?.trim()
        val fullName = binding.fullNameEditText.text?.toString()?.trim()
        val height = binding.heightEditText.text?.toString()?.trim()
        val weight = binding.weightEditText.text?.toString()?.trim()
        val dateOfBirth = selectedFinalDate
        val gender = selectedFinalGender

        if (username.isNullOrEmpty()) {
            authActivity?.showErrorAlert("Please enter your username.")
            return
        }
        if (username.length < 2) {
            authActivity?.showErrorAlert("Username must be atleast 2 character long.")
            return
        }

        if (fullName.isNullOrEmpty()) {
            authActivity?.showErrorAlert("Please enter your full name.")
            return
        }
        if (fullName.length < 2) {
            authActivity?.showErrorAlert("FullName must be atleast 2 character long.")
            return
        }

        if (dateOfBirth.isNullOrEmpty()) {
            authActivity?.showErrorAlert("Please select your date of birth.")
            return
        }

        if (gender.isNullOrEmpty()) {
            authActivity?.showErrorAlert("Please select your gender.")
            return
        }

        if (height.isNullOrEmpty()) {
            authActivity?.showErrorAlert("Please enter your height.")
            return
        }


        if (weight.isNullOrEmpty()) {
            authActivity?.showErrorAlert("Please enter your weight.")
            return
        }

//        if (imageUri == null){
//            authActivity?.showErrorAlert("Please select a profile image.")
//            return
//        }

        val request = CreateProfileRequest(
            userName = username,
            fullName = fullName,
            DOB = dateOfBirth,
            gender = gender,
            height = selectedHeightValue,
            weight = selectedWeightValue,
            userImageUri = imageUri,
            completeProfile=true
        )
        mViewModel.userCreateProfile(request, requireContext())
    }

    override fun onHeightSelected(feet: String, inches: String) {
        binding.heightEditText.setText("${feet}ft ${inches}in")
        selectedHeightValue = convertFeetInchesToCm(feet.toInt(), inches.toInt())
    }

    override fun onWeightSelected(weight: String) {
        selectedWeightValue = weight.toInt()
        binding.weightEditText.setText("$weight lbs")
    }


    private fun setupTouchListenerToHideKeyboard(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard()
                view.clearFocus()
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setupTouchListenerToHideKeyboard(view.getChildAt(i))
            }
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun convertFeetInchesToCm(feet: Int, inches: Int): Double {
        return feet * 30.48 + inches * 2.54
    }

    override fun onImageSelected(uri: String) {
        Log.d("ImageOickerDebugging", "From Fragment cameraImageUri: $uri")

        imageUri = uri.toUri()
        binding.imgProfile.setImageURI(imageUri)
    }

}

