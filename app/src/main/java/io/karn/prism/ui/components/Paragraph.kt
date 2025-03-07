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

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun Paragraph(
    text: String,
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    disabled: Boolean = false,
    textDecoration: TextDecoration = TextDecoration.None,
) {
    Paragraph(
        text = AnnotatedString(text),
        modifier = modifier,
        disabled = disabled,
        textDecoration = textDecoration,
    )
}

@Composable
fun Paragraph(
    text: AnnotatedString,
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    disabled: Boolean = false,
    textDecoration: TextDecoration = TextDecoration.None,
) {
    Text(
        modifier = modifier,
        fontWeight = FontWeight.Normal,
        textDecoration = textDecoration,
        style = MaterialTheme.typography.bodyMedium,
        color = LocalContentColor.current.copy(alpha = if (disabled) 0.54f else 1f),
        text = text,
    )
}