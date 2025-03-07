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

package io.karn.prism

import android.app.Application
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import io.karn.prism.data.Database
import io.karn.prism.domain.WallpaperWorker

class MainApplication : Application() {

    companion object {
        const val PRISM_ACCESS_REQUESTS_CHANNEL_ID = "prism_access_requests"
    }

    val database by lazy {
        Room.databaseBuilder(
            this,
            Database::class.java,
            "database",
        ).build()
    }

    override fun onCreate() {
        super.onCreate()

        // Begin the wallpaper worker
        WallpaperWorker.schedule(this)

        // Create the notification channel that is used to notify when access requests are
        // received.
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
            val channel = NotificationChannelCompat
                .Builder(PRISM_ACCESS_REQUESTS_CHANNEL_ID, importance)
                .setName(name)
                .setDescription(descriptionText)
                .build()
            // Register the channel with the system.
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.createNotificationChannel(channel)
        }
    }
}