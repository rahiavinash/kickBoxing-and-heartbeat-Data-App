package com.app.kickboxing.Activitys.presentation.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Entry point: Call this Composable inside your screen
 */
@Composable
fun LocationPermissionHandler(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
    onPermanentlyDenied: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showDeniedDialog by remember { mutableStateOf(false) }

    // Launcher for runtime permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            showDeniedDialog = false   // ✅ Hide dialog if granted
            onGranted()
        } else {
            val permanentlyDenied = !ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (permanentlyDenied) {
                showDeniedDialog = true
                onPermanentlyDenied()
            } else {
                showDeniedDialog = false
                onDenied()
            }
        }
    }

    // Initial check
    LaunchedEffect(Unit) {
        checkAndRequestPermission(
            context,
            permissionLauncher,
            onGranted = {
                showDeniedDialog = false   // ✅ Important fix
                onGranted()
            },
            onDenied = {
                showDeniedDialog = true
            }
        )
    }

    // Re-check permission when coming back from Settings
    LaunchedEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkAndRequestPermission(
                    context,
                    permissionLauncher,
                    onGranted = {
                        showDeniedDialog = false   // ✅ Important fix
                        onGranted()
                    },
                    onDenied = {
                        showDeniedDialog = true
                    }
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    // Show custom popup if permanently denied
    if (showDeniedDialog) {
        PermissionDeniedDialog(
            onDismiss = { showDeniedDialog = false },
            onGoToSettings = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }
        )
    }
}


private fun checkAndRequestPermission(
    context: Context,
    permissionLauncher: ActivityResultLauncher<Array<String>>,
    onGranted: () -> Unit,
    onDenied: (() -> Unit)? = null
) {
    val fineLocationGranted = ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseLocationGranted = ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (fineLocationGranted || coarseLocationGranted) {
        onGranted()
    } else {
        onDenied?.invoke()
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

@Composable
fun PermissionDeniedDialog(
    title: String = "Location Permission Required",
    message: String = "This feature needs location access to work properly. Please enable it in settings.",
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onGoToSettings() }) {
                    Text("Open Settings")
                }
            }
        }
    }
}
