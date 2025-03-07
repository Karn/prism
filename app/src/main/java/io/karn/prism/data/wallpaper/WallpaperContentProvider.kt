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
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Intent
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.BaseColumns
import androidx.core.graphics.drawable.toBitmap
import io.karn.prism.MainApplication
import io.karn.prism.data.notifications.createNewRequestNotificationForPackage
import io.karn.prism.data.thirdparty.ThirdPartyAppsRepository
import kotlinx.coroutines.runBlocking
import java.io.File


/**
 * This is a content provider that allows apps to subscribe to wallpaper changes.
 *
 * adb shell content query --uri "content://io.karn.prism.WallpaperContentProvider/wallpapers"
 *
 * Note that because of system limitations, this notifications of changes are not always accurate.
 * Callers should implement the [Intent.ACTION_WALLPAPER_CHANGED] broadcast receiver and they query
 * this content provider.
 */
class WallpaperContentProvider : ContentProvider() {

    data class Data(
        /**
         * The type of wallpaper. Either [WallpaperManager.FLAG_LOCK] or [WallpaperManager.FLAG_SYSTEM]
         */
        val type: Int,
        /**
         * The current wallpaper id as retrieved form the [WallpaperManager] and represents a unique
         * positive number that keys the wallpaper. See [WallpaperManager.getWallpaperId] for more
         * details.
         */
        val id: Int?,
        /**
         * The [ParcelFileDescriptor] for the wallpaper.
         */
        val pfd: ParcelFileDescriptor?,
    ) {
        init {
            when (type) {
                WallpaperManager.FLAG_LOCK -> Unit
                WallpaperManager.FLAG_SYSTEM -> Unit
                else -> throw IllegalStateException("Unknown wallpaper type: $type")
            }
        }
    }

    companion object {
        private const val PROVIDER_AUTHORITY = "io.karn.prism.WallpaperContentProvider"

        private const val WALLPAPERS: Int = 1
        private const val WALLPAPER: Int = 2

        private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH).also {
            it.addURI(PROVIDER_AUTHORITY, "wallpapers", WALLPAPERS)
            it.addURI(PROVIDER_AUTHORITY, "wallpapers/#", WALLPAPER)
        }

        // The available columns
        private const val COLUMN_TYPE: String = "type"
        private const val COLUMN_KEY: String = "key"
        private const val COLUMN_URI: String = "uri"

        val URI: Uri = Uri.parse("content://$PROVIDER_AUTHORITY/wallpapers")
    }

    private lateinit var wallpaperManager: WallpaperManager
    private lateinit var thirdPartyAppsRepository: ThirdPartyAppsRepository

    override fun onCreate(): Boolean {
        // TODO(karn) Write the calling package to the history list
        wallpaperManager = WallpaperManager.getInstance(requireContext())
        val database = (requireContext().applicationContext as MainApplication).database
        thirdPartyAppsRepository = ThirdPartyAppsRepository(database.thirdPartyAppDao())

        return true
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            WALLPAPERS -> "vnd.android.cursor.dir/vnd.$PROVIDER_AUTHORITY.wallpapers"
            WALLPAPER -> "vnd.android.cursor.item/vnd.$PROVIDER_AUTHORITY.wallpapers"
            else -> throw java.lang.IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor = runBlocking {
        if (!verifyAccessForPackage(callingPackage)) {
            return@runBlocking MatrixCursor(emptyArray())
        }

        // TODO(karn): Fix projection
        return@runBlocking when (uriMatcher.match(uri)) {
            WALLPAPERS -> {
                // Resolve the wallpapers
                val lock = getWallpaper(WallpaperManager.FLAG_LOCK)
                val home = getWallpaper(WallpaperManager.FLAG_SYSTEM)

                // Utility function to convert to the data into a cursor row
                fun Data.asRowArray(): Array<Any?> = arrayOf(
                    type,
                    when (type) {
                        WallpaperManager.FLAG_LOCK -> "lock"
                        WallpaperManager.FLAG_SYSTEM -> "system"
                        // Shouldn't happen but just in case.
                        else -> throw IllegalStateException("Unknown wallpaper type: $type")
                    },
                    id,
                    // Provide a resolution URI iff there is a valid file descriptor
                    pfd?.let { Uri.parse("content://$PROVIDER_AUTHORITY/wallpapers/$type") }
                )

                val cursor = MatrixCursor(
                    arrayOf(BaseColumns._ID, COLUMN_TYPE, COLUMN_KEY, COLUMN_URI),
                    2,
                )
                cursor.addRow(home.asRowArray())
                cursor.addRow(lock.asRowArray())

                cursor
            }

            else -> MatrixCursor(emptyArray())
        }
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return 0
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? = runBlocking {
        if (!verifyAccessForPackage(callingPackage)) {
            return@runBlocking null
        }

        val match = uriMatcher.match(uri)
        if (match != WALLPAPER) {
            return@runBlocking null
        }

        return@runBlocking when (val type = uri.lastPathSegment?.toIntOrNull()) {
            WallpaperManager.FLAG_SYSTEM -> getWallpaper(type).pfd
            WallpaperManager.FLAG_LOCK -> getWallpaper(type).pfd
            else -> null
        }
    }

    private suspend fun verifyAccessForPackage(callingPackage: String?): Boolean {
        // Automatically grant access to the current package
        val isCurrentApp = callingPackage == requireContext().packageName
        if (isCurrentApp) {
            return true
        }

        // If the callingPackage is null or the user has chosen to not allow thirdparty access,
        // deny the request.
        if (callingPackage == null) {
            return false
        }

        // Mark the package as accessed
        val resolvedPackage = thirdPartyAppsRepository.updateLastAccessTime(
            packageName = callingPackage,
            accessedAt = System.currentTimeMillis()
        )

        // First time requesting access throw up a notification
        if (!resolvedPackage.allowedAccess && resolvedPackage.requestCount > 0) {
            createNewRequestNotificationForPackage(
                context = requireContext(),
                callingPackage = callingPackage
            )
        }

        return resolvedPackage.allowedAccess
    }

    private fun getWallpaper(type: Int): Data {
        when (type) {
            WallpaperManager.FLAG_LOCK -> Unit
            WallpaperManager.FLAG_SYSTEM -> Unit
            else -> throw IllegalStateException("Unknown wallpaper type: $type")
        }

        val (id, pfd) = try {
            val id = wallpaperManager.getWallpaperId(type)
            val pfd = wallpaperManager.getWallpaperFile(type) ?: id.takeIf { it > 0 }?.let {
                getDefaultWallpaperParcelFileDescriptor()
            }

            id to pfd
        } catch (e: SecurityException) {
            -1 to null
        } catch (e: Exception) {
            -1 to null
        }

        return Data(type, id, pfd)
    }

    private fun getDefaultWallpaperParcelFileDescriptor(): ParcelFileDescriptor? {
        val wallpaperPath = File(context?.dataDir, "wallpaper_cache")

        // Open FD and return
        if (wallpaperPath.exists()) {
            return ParcelFileDescriptor.open(wallpaperPath, ParcelFileDescriptor.MODE_READ_ONLY)
        }

        // Write wallpaper to cache
        val defaultWallpaper = wallpaperManager.getBuiltInDrawable(WallpaperManager.FLAG_SYSTEM)
            ?: return null
        val defaultWallpaperBitmap = defaultWallpaper.toBitmap()
        try {
            wallpaperPath.outputStream().use {
                defaultWallpaperBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        } catch (e: Exception) {
            // Nothing to do
            return null
        }

        return ParcelFileDescriptor.open(wallpaperPath, ParcelFileDescriptor.MODE_READ_ONLY)
    }
}