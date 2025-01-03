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