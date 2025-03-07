/*
 * Prism - Wallpaper Manager
 * Copyright (C) 2025 Karn Saheb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
                id = android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                group = android.Manifest.permission_group.STORAGE,
                granted = hasSystemStoragePermission(),
                launchIntent = Intent(
                    ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:${context.packageName}"),
                )
            ),
            PermissionsModel(
                id = android.Manifest.permission.READ_MEDIA_IMAGES,
                group = android.Manifest.permission_group.STORAGE,
                granted = hasReadMediaImagesPermission(),
                launchIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${context.packageName}"),
                )
            ),
            PermissionsModel(
                id = android.Manifest.permission.POST_NOTIFICATIONS,
                group = android.Manifest.permission_group.NOTIFICATIONS,
                granted = hasPostNotificationsPermission(),
                launchIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
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

    private fun hasPostNotificationsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
