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

    suspend fun toggleAccess(packageName: String) {
        val current = dao.getByPackageName(packageName) ?: return
        val updated = current.copy(allowedAccess = !current.allowedAccess)

        dao.insert(updated)
    }
}