package cz.bosan.sikula_kmp.managers.server_manager

import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

data class TokenInfo(
    val backendToken: String,
    val backendTokenExpiration: LocalDateTime,
    val refreshToken: String?,
    val refreshTokenExpiration: LocalDateTime
) {
    companion object {
        val EMPTY = TokenInfo(
            backendToken = "",
            backendTokenExpiration = LocalDateTime.now(),
            refreshToken = "",
            refreshTokenExpiration = LocalDateTime.now()
        )
    }
}
