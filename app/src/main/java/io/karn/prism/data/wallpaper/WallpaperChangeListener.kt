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

import android.content.Context
import android.database.ContentObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class WallpaperChangeListener(context: Context) {
    val listener = callbackFlow {
        // Setup a content observer to listen to changes to the file
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)

                // Emit a change notification
                this@callbackFlow.trySend(Unit)
            }
        }

        context.contentResolver.registerContentObserver(
            WallpaperContentProvider.URI,
            false,
            observer
        )

        // Unsubscribe from changes on cancellation.
        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }
}