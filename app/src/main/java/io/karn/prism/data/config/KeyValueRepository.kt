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