package cz.bosan.sikula_kmp.core.data

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.managers.server_manager.TokenDto

interface TokenRefreshService {
    suspend fun refreshBackendToken(email: String, refreshToken: String): Result<TokenDto, DataError.Remote>
}