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
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import coil.compose.rememberAsyncImagePainter
import io.karn.prism.domain.WallpaperChangedBroadcastReceiver
import io.karn.prism.ui.theme.PrismTheme


/**
 * - Expose data via a content provider
 *   - https://developer.android.com/guide/topics/providers/create-document-provider
 * - Allow apps to query the data via the content resolver
 * - Eligible apps should implement a meta-data field in their XML that allows this app to query
 *   provide the user with a allowlist of apps that are allowed to access the wallpaper
 *   - When an app requests content, trigger a notification that prompts the user to confirm access
 *   - Allow user to configure an access window
 * - Subscribe to wallpaper changes in the background and emit the change notifications to apps
 *   that have been allowlisted
 * - Animations
 */
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel> { MainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val systemBarStyle = when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Transparent.toArgb()
            )

            Configuration.UI_MODE_NIGHT_YES -> SystemBarStyle.dark(Color.Transparent.toArgb())
            else -> error("Illegal State, current mode is $currentNightMode")
        }

        enableEdgeToEdge(
            statusBarStyle = systemBarStyle,
            navigationBarStyle = systemBarStyle,
        )

        setContent {
            PrismTheme {
                val navController = rememberNavController()
                val currentNavigationDestination by navController.currentBackStackEntryAsState()

                val uiState by viewModel.state.collectAsStateWithLifecycle()

                LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                    // Whenever the activity resumes, check the permissions to verify that the user
                    // still has access.
                    viewModel.validatePermissions()
                    viewModel.syncWallpapers()
                }

                DisposableEffect(Unit) {
                    // Subscribe to wallpaper changes while the app is active
                    registerReceiver(
                        WallpaperChangedBroadcastReceiver,
                        IntentFilter(Intent.ACTION_WALLPAPER_CHANGED),
                        RECEIVER_NOT_EXPORTED
                    )

                    onDispose {
                        unregisterReceiver(WallpaperChangedBroadcastReceiver)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        val height = 34.dp

                        Row(
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier,
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(height)
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
                                        .padding(start = height)
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    val title =
                                        when (currentNavigationDestination?.destination?.route) {
                                            "permissions" -> stringResource(R.string.manage_permissions_cta_title)
                                            "3rdparty" -> stringResource(R.string.permissions_third_party_title)
                                            else -> stringResource(R.string.app_name)
                                        }
                                    Text(
                                        text = title,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }

                                // TODO(karn): Animate the icon with the different colors/scaling
                                //  when tapped.
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .size(height)
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
                                ) {
                                    val icon =
                                        when (currentNavigationDestination?.destination?.route) {
                                            "home" -> R.drawable.ic_logo
                                            else -> com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_chevron_left_16_regular
                                        }

                                    Image(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable(enabled = icon != R.drawable.ic_logo) {
                                                navController.popBackStack()
                                            }
                                            .padding(4.dp),
                                        painter = rememberAsyncImagePainter(icon),
                                        contentDescription = stringResource(R.string.app_name),
                                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                                        contentScale = ContentScale.Fit,
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // TODO(karn): Might be nice to have a light/dark mode toggle here
                            OverflowButton(height)
                        }
                    },
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .padding(innerPadding)
                    ) {
                        NavHost(
                            modifier = Modifier.fillMaxSize(),
                            navController = navController,
                            startDestination = "home",
                            exitTransition = { fadeOut(snap(0)) },
                        ) {
                            composable("home") {
                                HomeLayout(
                                    modifier = Modifier.fillMaxSize(),
                                    state = uiState,
                                    navigateTo = { target ->
                                        when (target) {
                                            "permissions" -> navController.navigate("permissions")
                                            "3rdparty" -> navController.navigate("3rdparty")
                                            else -> Unit
                                        }
                                    },
                                )
                            }

                            composable("permissions") {
                                PermissionsLayout(
                                    modifier = Modifier,
                                    state = uiState,
                                )
                            }

                            composable(
                                route = "3rdparty",
                                deepLinks = listOf(
                                    navDeepLink { uriPattern = "prism://3rdparty" }
                                )
                            ) {
                                ThirdPartyAccessLayout(
                                    modifier = Modifier,
                                    state = uiState,
                                    toggleThirdPartyAccess = viewModel::updateThirdPartyAccess,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun OverflowButton(height: Dp) {
        // Don't render for now
        if (true) {
            return
        }

        val (expanded, setExpanded) = remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .size(height)
                .clip(RoundedCornerShape(4.dp))
                .clickable { setExpanded(!expanded) }
        ) {
            Image(
                modifier = Modifier.padding(4.dp),
                painter = rememberAsyncImagePainter(
                    com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_more_vertical_16_regular
                ),
                contentDescription = "",
                colorFilter = ColorFilter.tint(LocalContentColor.current),
                contentScale = ContentScale.None,
            )

            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterEnd),
                expanded = expanded,
                onDismissRequest = { setExpanded(false) },
                containerColor = Color(0xFF_EFF0F0),
                border = BorderStroke(
                    1.dp,
                    Color(0xFF_F9FAFA),
                ),
                shape = RoundedCornerShape(4.dp),
                tonalElevation = 0.dp,
                properties = PopupProperties(
                    dismissOnClickOutside = true,
                    usePlatformDefaultWidth = false,
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Text(text = "Export wallpaper")
                    // Text(text = "Manage Storage Permissions")
                    // Click this and launch a dialog
                    Text(text = "About")
                }
            }
        }
    }
}

@Composable
fun MockDevice(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val configuration = LocalConfiguration.current
    val deviceAspectRatio = remember(configuration) {
        with(configuration) {
            (screenWidthDp.toFloat() / screenHeightDp.toFloat()).coerceAtLeast(0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(deviceAspectRatio)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.background
                            .copy(alpha = 0.5f)
                            .compositeOver(MaterialTheme.colorScheme.surface),

                        MaterialTheme.colorScheme.surface,
                    )
                )
            )
            .then(modifier),
        content = {
            Box(Modifier.clip(RoundedCornerShape(2.dp))) {
                content()
            }
        }
    )
}

