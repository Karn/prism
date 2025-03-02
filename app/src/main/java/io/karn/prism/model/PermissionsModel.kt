package io.karn.prism.model

import android.content.Intent

data class PermissionsModel(
    val id: String,
    val group: String,
    val granted: Boolean,
    val launchIntent: Intent,
) {
    val name = id.split(".").lastOrNull() ?: ""
}