package io.karn.prism.data

import androidx.room.Database
import androidx.room.RoomDatabase
import io.karn.prism.data.config.KeyValue
import io.karn.prism.data.config.KeyValueDao
import io.karn.prism.data.thirdparty.ThirdPartyApp
import io.karn.prism.data.thirdparty.ThirdPartyAppDao

@Database(entities = [KeyValue::class, ThirdPartyApp::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun keyValueDao(): KeyValueDao
    abstract fun thirdPartyAppDao(): ThirdPartyAppDao
}