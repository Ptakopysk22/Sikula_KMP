package cz.bosan.sikula_kmp.core.data

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.managers.server_manager.TokenDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import cz.bosan.sikula_kmp.core.domain.Result

class SimpleTokenRefreshService(
    private val httpClient: HttpClient
) : TokenRefreshService {

    override suspend fun refreshBackendToken(
        email: String,
        refreshToken: String
    ): Result<TokenDto, DataError.Remote> {
        return try {
            val response: HttpResponse = httpClient.post("auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "refreshToken" to refreshToken))
            }
            val body = response.body<TokenDto>()
            Result.Success(body)
        } catch (e: Exception) {
            println("Error refresh token: $e")
            if (e.message?.contains("Invalid refresh token or user", ignoreCase = true) == true) {
                return Result.Error(DataError.Remote.UNAUTHORIZED)
            }
            return Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}