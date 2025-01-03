package io.karn.prism

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.karn.prism.ui.components.TwoItemRow
import io.karn.prism.ui.theme.checkBoxDefaults

@Composable
fun ThirdPartyAccessLayout(
    modifier: Modifier = Modifier,
    state: MainViewModel.State,
    toggleThirdPartyAccess: (packageName: String) -> Unit,
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        // TODO(karn): Add details about what third-party access means are required

        TwoItemRow(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .padding(8.dp),
            title = buildAnnotatedString { append("Enable third-party access") },
            label = "Allow external apps to access your wallpapers",
            content = {

            }
        )

        // Show apps with access
        // TODO(karn): Empty state
        LazyColumn(modifier = Modifier.weight(1f)) {
            val data = state.thirdPartyApps
            data.forEachIndexed { index, (app, isEnabled) ->
                item("thirdparty-$app") {
                    val appLabel = remember(app) {
                        try {
                            context.packageManager.getApplicationInfo(app, 0)
                                .loadLabel(context.packageManager)
                        } catch (e: Exception) {
                            ""
                        }
                    }

                    TwoItemRow(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clip(MaterialTheme.shapes.small)
                            .clickable(onClick = { toggleThirdPartyAccess(app) })
                            .padding(horizontal = 8.dp),
                        title = buildAnnotatedString {
                            if (index != data.lastIndex) {
                                append(" ├─ ")
                            } else {
                                append(" └─ ")
                            }

                            append(appLabel)

                            withStyle(style = SpanStyle(color = LocalContentColor.current.copy(alpha = 0.6f))) {
                                append(" ($app)")
                            }
                        },
                        label = "",
                        content = {
                            Checkbox(
                                checked = isEnabled,
                                onCheckedChange = null,
                                colors = checkBoxDefaults(),
                            )
                        }
                    )
                }
            }
        }
    }
}