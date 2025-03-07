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

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "third_party_app")
data class ThirdPartyApp(
    @PrimaryKey @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "allowed_access") val allowedAccess: Boolean,
    @ColumnInfo(name = "request_count") val requestCount: Int,
    @ColumnInfo(name = "last_accessed") val lastAccessed: Long,
)

@Dao
interface ThirdPartyAppDao {
    @Query("SELECT * FROM third_party_app")
    fun getAll(): Flow<List<ThirdPartyApp>>

    @Query("SELECT * FROM third_party_app WHERE package_name = :packageName")
    suspend fun getByPackageName(packageName: String): ThirdPartyApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: ThirdPartyApp)
}