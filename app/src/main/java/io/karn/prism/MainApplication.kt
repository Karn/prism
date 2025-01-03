package io.karn.prism

import android.app.Application
import androidx.room.Room
import io.karn.prism.data.Database
import io.karn.prism.domain.WallpaperWorker

class MainApplication : Application() {

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
    }
}