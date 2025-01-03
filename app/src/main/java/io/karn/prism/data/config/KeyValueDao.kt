package io.karn.prism.data.config

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "key_value")
data class KeyValue(
    @PrimaryKey
    val key: String,
    val value: String?,
)

@Dao
interface KeyValueDao {
    @Query("SELECT value FROM key_value WHERE `key` = :key")
    fun getFlow(key: String): Flow<String?>

    @Query("SELECT value FROM key_value WHERE `key` = :key")
    suspend fun get(key: String): String?

    @Query("INSERT OR REPLACE INTO key_value (`key`, value) VALUES (:key, :value)")
    suspend fun set(key: String, value: String?)
}