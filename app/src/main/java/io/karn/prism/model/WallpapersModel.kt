package io.karn.prism.model

import android.app.WallpaperManager
import android.net.Uri

data class WallpapersModel(
    /**
     * The timestamp of when the wallpapers were last fetched.
     */
    val lastFetched: Long,
    /**
     * The lock screen wallpaper, corresponds to [WallpaperManager.FLAG_LOCK].
     */
    val lock: Uri?,
    /**
     * The home screen wallpaper, corresponds to [WallpaperManager.FLAG_SYSTEM].
     */
    val system: Uri?,
)