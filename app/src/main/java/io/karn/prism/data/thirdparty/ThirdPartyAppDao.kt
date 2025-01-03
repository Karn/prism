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