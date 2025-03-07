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

package io.karn.prism.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.AnnotatedString

@Composable
fun TwoItemRow(
    modifier: Modifier,
    title: AnnotatedString,
    label: String,
    enabled: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier.let {
            if (enabled) {
                return@let it
            }

            it.alpha(0.4f)
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (title.isNotBlank()) {
                Paragraph(
                    modifier = Modifier,
                    text = title
                )
            }
            if (label.isNotBlank()) {
                Paragraph(
                    modifier = Modifier,
                    text = label,
                    disabled = true,
                )
            }
        }

        content()
    }
}