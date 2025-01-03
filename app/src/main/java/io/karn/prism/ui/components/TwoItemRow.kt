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