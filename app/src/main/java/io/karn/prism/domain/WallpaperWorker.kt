package io.karn.prism.domain

import android.content.Context
import android.net.Uri
import android.provider.Settings
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.karn.prism.data.wallpaper.WallpaperContentProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import java.io.File

class WallpaperWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private val workerRequest = OneTimeWorkRequestBuilder<WallpaperWorker>()
            .setConstraints(
                constraints = Constraints.Builder()
                    .addContentUriTrigger(
                        uri = Settings.Secure.getUriFor("theme_customization_overlay_packages"),
                        triggerForDescendants = true,
                    )
                    .addContentUriTrigger(
                        uri = Uri.fromFile(File("/data/system/users/0/wallpaper")),
                        triggerForDescendants = true,
                    )
                    .build()
            )
            .build()

        fun schedule(context: Context) = WorkManager.getInstance(context).apply {
            enqueueUniqueWork(
                uniqueWorkName = "wallpaper_worker",
                existingWorkPolicy = ExistingWorkPolicy.REPLACE,
                request = workerRequest,
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO.limitedParallelism(1)) {
        // Emit a notification to indicate that the wallpaper has changed
        applicationContext.contentResolver.notifyChange(WallpaperContentProvider.URI, null)

        // TODO(karn): Figure out if there's a better way to reschedule instead of replacing
        //  this task.
        schedule(applicationContext)

        Result.success()
    }
}
