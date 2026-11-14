package com.app.kickboxing.Activitys.presentation.Authentication;

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.app.kickboxing.Activitys.presentation.Ui.ViewModel.LoginViewModel
import com.app.kickboxing.Activitys.utils.AlertManager
import com.app.kickboxing.databinding.ImageBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageSheet(val imageSelectedListener: OnImageSelectedListener? = null)  : BottomSheetDialogFragment() {

    private lateinit var mBinding: ImageBottomSheetBinding
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var alertManager: AlertManager
    var dialogs: Dialog? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val GALLERY_PERMISSION_REQUEST_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private val GALLERY_REQUEST_CODE = 103
    private var cameraImageUri: Uri? = null
    private var tempCameraFile: File? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.2f)

            findViewById<View>(com.google.android.material.R.id.container)?.fitsSystemWindows = false
            findViewById<View>(com.google.android.material.R.id.coordinator)?.fitsSystemWindows = false
        }

        dialog.behavior.isFitToContents = false
        dialog.behavior.skipCollapsed = true

        return dialog
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding =
            ImageBottomSheetBinding.inflate(layoutInflater, container, false)
        dialogs = Dialog(requireActivity())
        dialogs?.requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                Log.d("ImagePickerDebugging", "cameraImageUri: $cameraImageUri")
                imageSelectedListener?.onImageSelected(cameraImageUri.toString())
                dismiss()
            } else {
                Log.e("ImagePickerDebugging", "Camera capture failed or URI is null.")
            }
        }


        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageSelectedListener?.onImageSelected(uri.toString())
                dismiss()
            }
        }

        return mBinding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("camera_uri", cameraImageUri)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertManager = AlertManager(mBinding.root as ViewGroup, requireContext())
        cameraImageUri = savedInstanceState?.getParcelable("camera_uri") ?: cameraImageUri
        val offsetFromTop = 200
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = true
            expandedOffset = offsetFromTop
            (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        dialog?.window?.setDimAmount(0.2f)
        mBinding.tvCamera.setOnClickListener {
            openCamera()
        }

        mBinding.tvPhotoLibrary.setOnClickListener {
            openGallery()
        }

        mBinding.btCancel.setOnClickListener{
            dismiss()
        }
    }

    private fun openCamera(){
        if (checkCameraPermission()) {
            openCameraPicker()
        } else {
            requestCameraPermission()
        }
    }

    private fun openGallery(){
        if (checkGalleryPermission()) {
            openGalleryPicker()
        } else {
            requestGalleryPermission()
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }
    }

    private fun checkGalleryPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestCameraPermission() {
        val permissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(Manifest.permission.CAMERA)
        }
        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions,
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun requestGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(permission),
            GALLERY_PERMISSION_REQUEST_CODE
        )
    }

    fun openCameraPicker() {
        if (!checkCameraPermission()) {
            requestCameraPermission()
            return
        }

        try {
            tempCameraFile = createImageFile()
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "com.app.kickboxing.fileprovider",
                tempCameraFile!!
            )
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            Toast.makeText(context, "Could Not Create Profile", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun openGalleryPicker() {
        if (!checkGalleryPermission()) {
            requestGalleryPermission()
            return
        }

        galleryLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        storageDir?.mkdirs()

        return File(storageDir, "JPEG_${timeStamp}_.jpg")
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    // Permission denied, show message to user
                }
            }
            GALLERY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    // Permission denied, show message to user
                }
            }
        }
    }

//    override fun onAttach(context: Context) {
//        val newContext = LocaleHelper.setLocale(context, Prefs.init().appLanguageCode ?: "en")
//        super.onAttach(newContext)
//    }


}

interface OnImageSelectedListener{
    fun onImageSelected(uri: String)
}