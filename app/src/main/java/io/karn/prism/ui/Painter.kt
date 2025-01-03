package io.karn.prism.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import coil.compose.AsyncImagePainter.Companion.DefaultTransform
import coil.compose.AsyncImagePainter.State

@Composable
fun rememberKeyedAsyncImagePainter(
    key: Any?,
    model: Any?,
    transform: (State) -> State = DefaultTransform,
    onState: ((State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter {
    val painter = coil.compose.rememberAsyncImagePainter(
        model, transform, onState, contentScale, filterQuality
    )

    LaunchedEffect(key) {
        // Manually trigger a recomposition when the key changes.
        painter.onForgotten()
        painter.onRemembered()
    }

    return painter
}