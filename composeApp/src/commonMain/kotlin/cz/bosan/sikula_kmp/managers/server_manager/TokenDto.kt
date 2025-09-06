package cz.bosan.sikula_kmp.managers.server_manager

import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.LocalDateTimeIso8601Serializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(
    @SerialName("accessToken") val backendToken: String,
    @SerialName("refreshToken") val refreshToken: String?,
    @SerialName("refreshTokenExpiresAt") @Serializable(with = LocalDateTimeIso8601Serializer::class) val refreshTokenExpiration: LocalDateTime,
)