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

package io.karn.prism.data.thirdparty

import kotlinx.coroutines.flow.Flow

class ThirdPartyAppsRepository(
    private val dao: ThirdPartyAppDao,
) {

    fun getAll(): Flow<List<ThirdPartyApp>> {
        return dao.getAll()
    }

    suspend fun updateLastAccessTime(
        packageName: String,
        accessedAt: Long = System.currentTimeMillis()
    ): ThirdPartyApp {
        val current = dao.getByPackageName(packageName) ?: ThirdPartyApp(
            packageName = packageName,
            allowedAccess = false,
            requestCount = 1,
            lastAccessed = accessedAt,
        )

        val updated = current.copy(lastAccessed = accessedAt)

        dao.insert(updated)

        return updated
    }

    suspend fun updateAccess(packageName: String, enabled: Boolean) {
        val current = dao.getByPackageName(packageName) ?: return
        val updated = current.copy(allowedAccess = enabled)

        dao.insert(updated)
    }
}