package cz.bosan.sikula_kmp.core.data

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.managers.leader_manager.data.DefaultLeaderRepository
import cz.bosan.sikula_kmp.managers.server_manager.toTokenInfo
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TokenManager(
    private val refreshService: TokenRefreshService,
    private val leaderRepositoryLazy: Lazy<DefaultLeaderRepository>
) {

    private val leaderRepository get() = leaderRepositoryLazy.value

    fun isTokenValid(): Boolean {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val expiration = TokenHolder.backendTokenExpiration
        return TokenHolder.backendToken != null && expiration != null && expiration > now
    }

    suspend fun refreshTokenIfNeeded(): Boolean {
        val email = TokenHolder.email
        val refreshToken = TokenHolder.refreshToken
        var success = false

        if (email != null && refreshToken != null) {
            refreshService.refreshBackendToken(email, refreshToken)
                .onSuccess { tokenDto ->
                    val tokenInfo = tokenDto.toTokenInfo()
                    TokenHolder.updateAll(tokenInfo = tokenInfo, email = email)
                    val currentLeader = leaderRepository.getCurrentLeaderLocal()
                    leaderRepository.setCurrentLeader(
                        currentLeader = currentLeader.copy(tokenInfo = tokenInfo)
                    )
                    success = true
                }
                .onError { error ->
                    if (error != DataError.Remote.UNAUTHORIZED) {
                        success = true
                    } else {
                        success = false
                    }
                }
        }
        return success
    }
}