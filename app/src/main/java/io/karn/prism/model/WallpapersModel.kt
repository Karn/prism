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