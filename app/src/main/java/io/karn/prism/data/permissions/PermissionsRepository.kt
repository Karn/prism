package io.karn.prism.data.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import androidx.core.content.ContextCompat
import io.karn.prism.model.PermissionsModel

class PermissionsRepository(private val context: Context) {
    fun getPermissions(): List<PermissionsModel> {
        return listOf(
            PermissionsModel(
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE.split(".").lastOrNull()
                    ?: "",
                hasSystemStoragePermission(),
                Intent(
                    ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:${context.packageName}"),
                )
            ),
            PermissionsModel(
                android.Manifest.permission.READ_MEDIA_IMAGES.split(".").lastOrNull() ?: "",
                hasReadMediaImagesPermission(),
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${context.packageName}"),
                )
            )
        )
    }

    private fun hasSystemStoragePermission(): Boolean {
        return Environment.isExternalStorageManager()
    }

    private fun hasReadMediaImagesPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    }
}
