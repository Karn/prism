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

package io.karn.prism.data.config

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KeyValueRepository(
    private val dao: KeyValueDao,
) {
    sealed class Key<T>(val name: String, val defaultValue: T?) {
        data object Sample : Key<Boolean>(
            name = "sample",
            defaultValue = false
        )
    }

    fun getFlow(key: Key<Boolean>): Flow<Boolean?> {
        return dao.getFlow(key.name).map {
            it?.toBoolean() ?: key.defaultValue
        }
    }

    suspend fun get(key: Key<Boolean>): Boolean? {
        return dao.get(key.name)?.toBoolean() ?: key.defaultValue
    }

    suspend fun set(key: Key<Boolean>, value: Boolean) {
        dao.set(key.name, value.toString())
    }
}