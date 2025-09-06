package cz.bosan.sikula_kmp.managers.server_manager

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess

class ServerRepository(
    private val serverDataSource: ServerDataSource
) {
    suspend fun isServerResponding(): Boolean {
        var isResponding = false
        serverDataSource.isServerResponding()
            .onSuccess { isResponding = true }
            .onError { error ->
                if (error == DataError.Remote.NOT_FOUND) {
                    isResponding = true
                }
            }
        return isResponding
    }

    suspend fun getBackendToken(googleToken: String): Result<TokenInfo, DataError.Remote> {
        return serverDataSource.getBackendToken(googleToken).map { it.toTokenInfo() }
    }

}