import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.app.kickboxing.Activitys.presentation.Ui.CommonComposables.ButtonComposable
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.BlackColor
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.EditTextBackground
import com.app.kickboxing.Activitys.presentation.Ui.home.ui.theme.WhiteColor
import com.app.kickboxing.R


@Composable
fun ImageBottomSheet(
    onDone: (Uri?) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val uri = saveImageToMediaStore(context, it)
            onDone(uri)
            Log.e("uri", "Failed to fetch profile data ${uri}")

        } ?: onDone(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        onDone(uri)
        Log.e("uri", "Failed to fetch profile data ${uri}")

    }


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {

            if (permissions.containsKey(Manifest.permission.CAMERA)) {
                cameraLauncher.launch(null)
            } else {
                galleryLauncher.launch("image/*")
            }
        } else {
            onDone(null)
        }
    }

    fun checkAndRequestCameraPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            cameraLauncher.launch(null)
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    fun checkAndRequestStoragePermission() {
        val readImagesPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            context, readImagesPermission
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            galleryLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(arrayOf(readImagesPermission))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select type",
            fontSize = 25.sp,
            style = TextStyle(color = BlackColor),
            fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(35.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(EditTextBackground)
                .clickable {
                    checkAndRequestCameraPermission()
                }
                .padding(vertical = 20.dp)
        ) {
            Text(
                text = "Camera",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontWeight = FontWeight.Medium,
                style = TextStyle(color = WhiteColor),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(EditTextBackground)
                .clickable {
                    checkAndRequestStoragePermission()
                }
                .padding(vertical = 20.dp)
        ) {
            Text(
                text = "Photo Library",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.dmsans_semibold)),
                fontWeight = FontWeight.Medium,
                style = TextStyle(color = WhiteColor),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        ButtonComposable("Cancel",  {
            if (onCancel != null) {
                onCancel()
            }
        })
    }
}


fun saveImageToMediaStore(context: android.content.Context, bitmap: android.graphics.Bitmap): Uri? {
    val contentValues = android.content.ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "camera_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        context.contentResolver.openOutputStream(it).use { outputStream ->
            if (outputStream != null) bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
    }
    return uri
}

@Preview
@Composable
fun preview(){
    ImageBottomSheet({ })
}
