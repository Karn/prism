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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import io.karn.prism.data.config.KeyValueRepository
import io.karn.prism.data.permissions.PermissionsRepository
import io.karn.prism.data.thirdparty.ThirdPartyApp
import io.karn.prism.data.thirdparty.ThirdPartyAppsRepository
import io.karn.prism.data.wallpaper.WallpaperChangeListener
import io.karn.prism.data.wallpaper.WallpaperRepository
import io.karn.prism.model.PermissionsModel
import io.karn.prism.model.WallpapersModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class MainViewModel(
    private val keyValueRepository: KeyValueRepository,
    private val permissionsRepository: PermissionsRepository,
    private val wallpaperRepository: WallpaperRepository,
    private val wallpaperChangeListener: WallpaperChangeListener,
    private val thirdPartyAppsRepository: ThirdPartyAppsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            // Get the Application object from extras
            val application = checkNotNull(extras[APPLICATION_KEY])
            // Create a SavedStateHandle for this ViewModel from extras
            val savedStateHandle = extras.createSavedStateHandle()

            val database = (application as MainApplication).database
            val permissionsRepository = PermissionsRepository(application)
            val wallpaperRepository = WallpaperRepository(application)
            val wallpaperChangeListener = WallpaperChangeListener(application)
            val keyValueRepository = KeyValueRepository(database.keyValueDao())
            val thirdPartyAppsRepository = ThirdPartyAppsRepository(database.thirdPartyAppDao())

            return MainViewModel(
                keyValueRepository = keyValueRepository,
                permissionsRepository = permissionsRepository,
                wallpaperRepository = wallpaperRepository,
                wallpaperChangeListener = wallpaperChangeListener,
                thirdPartyAppsRepository = thirdPartyAppsRepository,
                savedStateHandle = savedStateHandle
            ) as T
        }
    }

    // Create and expose state
    data class State(
        val permissions: List<PermissionsModel> = emptyList(),
        val enableThirdPartyAccess: Boolean = false,
        val thirdPartyApps: List<ThirdPartyApp> = emptyList(),
        val wallpapers: WallpapersModel = WallpapersModel(
            lastFetched = -1,
            lock = null,
            system = null
        ),
    )

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val viewModelWorkerScope = viewModelScope + Dispatchers.IO.limitedParallelism(1)

    private val manualSyncChannel = Channel<Unit>(Channel.CONFLATED)

    init {
        // As part of the initialization, listen to changes to the wallpaper and fetch the latest
        // wallpaper URIs.

        merge(
            wallpaperChangeListener.listener,
            manualSyncChannel.receiveAsFlow()
        ).conflate().map {
            val wallpapers = wallpaperRepository.getWallpapers()

            _state.update { it.copy(wallpapers = wallpapers) }
        }.launchIn(viewModelWorkerScope)

        // Load the list of third-party apps
        thirdPartyAppsRepository.getAll()
            .onEach { apps ->
                _state.update { it.copy(thirdPartyApps = apps) }
            }
            .launchIn(viewModelScope)
    }


    fun validatePermissions() = viewModelScope.launch {
        val hasStoragePermissions = permissionsRepository.getPermissions()

        _state.update { current ->
            current.copy(
                permissions = hasStoragePermissions,
            )
        }
    }

    fun syncWallpapers() = viewModelWorkerScope.launch {
        manualSyncChannel.trySend(Unit)
    }

    // TODO(karn): Switch to a suspend function that allows call site to manage button
    //  enabled/disabled state.
    fun updateThirdPartyAccess(packageName: String, enabled: Boolean) = viewModelScope.launch {
        thirdPartyAppsRepository.updateAccess(packageName, enabled)
    }
}