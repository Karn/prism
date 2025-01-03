package io.karn.prism.model

import android.content.Intent

data class PermissionsModel(
    val name: String,
    val granted: Boolean,
    val launchIntent: Intent,
)