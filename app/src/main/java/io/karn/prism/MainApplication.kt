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