package io.karn.prism

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import io.karn.prism.ui.components.TwoItemRow
import io.karn.prism.ui.rememberKeyedAsyncImagePainter

@Composable
fun HomeLayout(
    modifier: Modifier = Modifier,
    state: MainViewModel.State,
    navigateTo: (String?) -> Unit,
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            // TODO(karn): Press and hold to hide the overlays
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                    alignment = Alignment.CenterHorizontally
                ),
            ) {
                val (key, lockScreen, homeScreen) = state.wallpapers

                Column(Modifier.weight(1f)) {
                    MockDevice(modifier = Modifier) {
                        val painter = rememberKeyedAsyncImagePainter(
                            key = key,
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(lockScreen ?: homeScreen)
                                .crossfade(true)
                                .build()
                        )

                        Image(
                            painter = painter,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = stringResource(R.string.wallpaper_type_lockscreen),
                    )
                }

                Column(Modifier.weight(1f)) {
                    MockDevice(modifier = Modifier) {
                        val painter = rememberKeyedAsyncImagePainter(
                            key = key,
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(homeScreen)
                                .crossfade(true)
                                .build()
                        )

                        Image(
                            painter = painter,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = stringResource(R.string.wallpaper_type_homescreen),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .height(34.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(5.dp),
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        val pickWallpaper = Intent(Intent.ACTION_SET_WALLPAPER)
                        val chooser =
                            Intent.createChooser(pickWallpaper, "set wallpaper")
                        context.startActivity(chooser)
                    }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.manage_wallpaper_cta),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        TwoItemRow(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = { navigateTo("permissions") })
                .padding(8.dp),
            title = buildAnnotatedString { append(stringResource(R.string.manage_permissions_cta_title)) },
            label = stringResource(R.string.manage_permissions_cta_description),
            content = {
                Image(
                    modifier = Modifier
                        .size(34.dp)
                        .padding(4.dp),
                    painter = rememberAsyncImagePainter(
                        com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_shield_keyhole_20_regular,
                    ),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(LocalContentColor.current),
                    contentScale = ContentScale.Fit,
                )
            }
        )

        TwoItemRow(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = { navigateTo("3rdparty") })
                .padding(8.dp),
            title = buildAnnotatedString { append(stringResource(R.string.permissions_third_party_title)) },
            label = stringResource(R.string.permissions_third_party_description),
            content = {
                Image(
                    modifier = Modifier
                        .size(34.dp)
                        .padding(4.dp),
                    painter = rememberAsyncImagePainter(
                        com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_shifts_availability_20_regular,
                    ),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(LocalContentColor.current),
                    contentScale = ContentScale.Fit,
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}