package cz.bosan.sikula_kmp.managers.server_manager

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlin.io.encoding.ExperimentalEncodingApi

fun TokenDto.toTokenInfo(): TokenInfo {
    return TokenInfo(
        backendToken = backendToken,
        backendTokenExpiration = extractExpirationFromJwt(backendToken),
        refreshToken = refreshToken,
        refreshTokenExpiration = refreshTokenExpiration
    )
}

fun extractExpirationFromJwt(token: String): LocalDateTime {
    val parts = token.split(".")
    require(parts.size == 3) { "Invalid JWT token format." }

    val payload = parts[1]
    val decodedBytes = base64UrlDecode(payload)
    val payloadJson = Json.parseToJsonElement(decodedBytes.decodeToString())

    val expSeconds = payloadJson.jsonObject["exp"]?.jsonPrimitive?.longOrNull
        ?: throw IllegalArgumentException("Token payload does not contain 'exp' field.")
    val instant = Instant.fromEpochSeconds(expSeconds)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

@OptIn(ExperimentalEncodingApi::class)
private fun base64UrlDecode(data: String): ByteArray {
    val replaced = data.replace('-', '+').replace('_', '/')
    val padded = replaced.padEnd(replaced.length + (4 - replaced.length % 4) % 4, '=')
    return kotlin.io.encoding.Base64.Default.decode(padded)
}