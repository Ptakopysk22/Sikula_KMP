package cz.bosan.sikula_kmp.core.data

import cz.bosan.sikula_kmp.managers.server_manager.TokenInfo
import kotlinx.datetime.LocalDateTime

object TokenHolder {
    var backendToken: String? = null
    var backendTokenExpiration: LocalDateTime? = null
    var refreshToken: String? = null
    var refreshTokenExpiration: LocalDateTime? = null
    var email: String? = null

    fun updateAll(
        tokenInfo : TokenInfo,
        email: String?
    ) {
        this.backendToken = tokenInfo.backendToken
        this.backendTokenExpiration = tokenInfo.backendTokenExpiration
        this.refreshToken = tokenInfo.refreshToken
        this.refreshTokenExpiration = tokenInfo.refreshTokenExpiration
        this.email = email
    }

    fun clear() {
        backendToken = null
        backendTokenExpiration = null
        refreshToken = null
        refreshTokenExpiration = null
        email = null
    }
}