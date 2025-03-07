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

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.karn.prism.ui.components.TwoItemRow
import io.karn.prism.ui.theme.checkBoxDefaults

@Composable
fun PermissionsLayout(
    modifier: Modifier = Modifier,
    state: MainViewModel.State,
) {
    val context = LocalContext.current

    val storagePermissions = remember(state.permissions) {
        state.permissions
            .filter { it.group == android.Manifest.permission_group.STORAGE }
    }
    val notificationPermissions = remember(state.permissions) {
        state.permissions
            .filter { it.group == android.Manifest.permission_group.NOTIFICATIONS }
    }

    Column(modifier = modifier) {
        // TODO(karn): Add details about why permissions are required

        TwoItemRow(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .padding(8.dp),
            title = buildAnnotatedString { append(stringResource(R.string.permissions_enable_storage_title)) },
            label = stringResource(R.string.permissions_enable_storage_description),
            content = {
                val toggleState = remember(storagePermissions) {
                    val all = storagePermissions.all { it.granted }
                    val some = storagePermissions.any { it.granted }

                    when {
                        all -> ToggleableState.On
                        some -> ToggleableState.Indeterminate
                        else -> ToggleableState.Off
                    }
                }

                TriStateCheckbox(
                    state = toggleState,
                    onClick = null,
                    colors = checkBoxDefaults(),
                )
            }
        )

        storagePermissions.forEachIndexed { index, permission ->
            TwoItemRow(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        val intent = permission.launchIntent.also {
                            it.flags += Intent.FLAG_ACTIVITY_NEW_TASK
                        }

                        context.startActivity(intent)
                    }
                    .padding(horizontal = 8.dp),
                title = buildAnnotatedString {
                    if (index != storagePermissions.lastIndex) {
                        append(" ├─ ")
                    } else {
                        append(" └─ ")
                    }

                    withStyle(
                        // TODO(karn): Rounded corners
                        style = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = MaterialTheme.colorScheme.surface,
                        )
                    ) {
                        append(" ${permission.name} ")
                    }
                },
                label = "",
                content = {
                    Checkbox(
                        checked = permission.granted,
                        onCheckedChange = null,
                        colors = checkBoxDefaults(),
                    )
                }
            )
        }

        Spacer(Modifier.size(8.dp))

        TwoItemRow(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .padding(8.dp),
            title = buildAnnotatedString { append(stringResource(R.string.permissions_enable_notifications_title)) },
            label = stringResource(R.string.permissions_enable_notifications_description),
            content = { },
        )

        notificationPermissions.forEachIndexed { index, permission ->
            TwoItemRow(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        val intent = permission.launchIntent.also {
                            it.flags += Intent.FLAG_ACTIVITY_NEW_TASK
                        }

                        context.startActivity(intent)
                    }
                    .padding(horizontal = 8.dp),
                title = buildAnnotatedString {
                    if (index != notificationPermissions.lastIndex) {
                        append(" ├─ ")
                    } else {
                        append(" └─ ")
                    }

                    withStyle(
                        // TODO(karn): Rounded corners
                        style = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = MaterialTheme.colorScheme.surface,
                        )
                    ) {
                        append(" ${permission.name} ")
                    }
                },
                label = "",
                content = {
                    Checkbox(
                        checked = permission.granted,
                        onCheckedChange = null,
                        colors = checkBoxDefaults(),
                    )
                }
            )
        }
    }
}