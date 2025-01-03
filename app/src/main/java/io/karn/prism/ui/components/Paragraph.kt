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