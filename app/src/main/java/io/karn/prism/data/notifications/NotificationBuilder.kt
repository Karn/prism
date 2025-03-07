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

package io.karn.prism.data.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import io.karn.prism.MainActivity
import io.karn.prism.MainApplication
import io.karn.prism.R
import io.karn.prism.data.thirdparty.ThirdPartyApprovalBroadcastReceiver
import kotlin.random.Random

fun createNewRequestNotificationForPackage(context: Context, callingPackage: String) {
    val hasNotificationPermissions = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasNotificationPermissions) return

    val notificationId = Random.nextInt(0, Int.MAX_VALUE)
    val builder = NotificationCompat
        .Builder(context, MainApplication.PRISM_ACCESS_REQUESTS_CHANNEL_ID)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentTitle(
            context.getString(R.string.notification_new_request_title)
        )
        .setContentText(
            context.getString(
                R.string.notification_new_request_description,
                callingPackage
            )
        )
        .setContentIntent(getContentIntent(context))
        .addAction(
            com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_checkmark_28_filled,
            context.getString(R.string.notification_new_request_approve),
            getApprovalAction(context, notificationId, callingPackage),
        )
        .setAutoCancel(true)

    NotificationManagerCompat
        .from(context)
        .notify(notificationId, builder.build())
}

private fun getContentIntent(context: Context): PendingIntent? {
    val intent = Intent(
        Intent.ACTION_VIEW,
        "prism://3rdparty".toUri(),
        context,
        MainActivity::class.java
    )
    return PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )
}

private fun getApprovalAction(
    context: Context,
    notificationId: Int,
    packageName: String
): PendingIntent? {
    val intent = Intent(context, ThirdPartyApprovalBroadcastReceiver::class.java).apply {
        setAction(context.getString(R.string.permissions_third_party_approval_action))
        putExtra(
            context.getString(R.string.permissions_third_party_approval_action_extra_notification_id),
            notificationId
        )
        putExtra(
            context.getString(R.string.permissions_third_party_approval_action_extra_package_name),
            packageName
        )
    }

    return PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or Intent.FILL_IN_DATA
    )
}