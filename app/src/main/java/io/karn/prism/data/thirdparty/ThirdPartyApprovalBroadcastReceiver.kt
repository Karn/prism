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

package io.karn.prism.data.thirdparty

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver
import io.karn.prism.MainApplication
import io.karn.prism.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ThirdPartyApprovalBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != context.getString(R.string.permissions_third_party_approval_action)) return
        val notificationId = intent.getIntExtra(
            context.getString(R.string.permissions_third_party_approval_action_extra_notification_id),
            -1
        ).takeIf { it > 0 } ?: return

        val targetPackageName = intent.getStringExtra(
            context.getString(R.string.permissions_third_party_approval_action_extra_package_name)
        ) ?: return

        val pendingResult = goAsync()
        GlobalScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            val database = (context.applicationContext as MainApplication).database
            val thirdPartyAppsRepository = ThirdPartyAppsRepository(database.thirdPartyAppDao())

            thirdPartyAppsRepository.updateAccess(
                packageName = targetPackageName,
                enabled = true
            )

            NotificationManagerCompat.from(context).cancel(notificationId)
        }.invokeOnCompletion {
            pendingResult.finish()
        }
    }
}