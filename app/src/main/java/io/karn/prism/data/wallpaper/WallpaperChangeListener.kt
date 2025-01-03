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