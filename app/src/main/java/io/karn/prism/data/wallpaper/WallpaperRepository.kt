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

package io.karn.prism.data.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import androidx.core.database.getStringOrNull
import io.karn.prism.model.WallpapersModel

/**
 * Provides access to the system wallpapers
 */
class WallpaperRepository(
    private val context: Context,
) {
    fun getWallpapers(): WallpapersModel {
        val timestamp = System.currentTimeMillis()
        val wallpapers = mutableMapOf<Int, Uri?>()

        context.contentResolver.query(
            WallpaperContentProvider.URI,
            null,
            null,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                val uri = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("uri"))
                wallpapers[id] = uri?.let { Uri.parse(uri) }
            }
        }

        return WallpapersModel(
            lastFetched = timestamp,
            lock = wallpapers[WallpaperManager.FLAG_LOCK],
            system = wallpapers[WallpaperManager.FLAG_SYSTEM],
        )
    }
}