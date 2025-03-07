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

package io.karn.prism.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PrismTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme(
            primary = Color(0xFF_8DA6C7),
            secondary = PurpleGrey80,
            tertiary = Pink80,
            background = Color(0xFF_111212),
            surface = Color(0xFF_222429),
        )

        else -> lightColorScheme(
            primary = Color(0xFF_8DA6C7),
            secondary = PurpleGrey40,
            tertiary = Pink40,
            background = Color(0xFF_F9FAFA),
            surface = Color(0xFF_EFF0F0),
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = MaterialTheme.shapes.copy(
            small = RoundedCornerShape(2.dp)
        ),
        typography = Typography,
        content = content
    )
}