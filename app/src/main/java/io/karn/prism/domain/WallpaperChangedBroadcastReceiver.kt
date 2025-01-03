package io.karn.prism.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.karn.prism.data.wallpaper.WallpaperContentProvider

val WallpaperChangedBroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val uri = WallpaperContentProvider.URI
        requireNotNull(context).contentResolver.notifyChange(uri, null)
    }
}