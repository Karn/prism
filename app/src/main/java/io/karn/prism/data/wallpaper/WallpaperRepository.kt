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