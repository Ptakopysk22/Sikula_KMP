package cz.bosan.sikula_kmp.managers.server_manager

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ServerDataSource(
    private val httpClient: TokenAwareHttpClient
) {

    suspend fun isServerResponding(): Result<Unit, DataError.Remote> {
        return httpClient.get("", checkToken = false) {
            }
    }

    suspend fun getBackendToken(googleToken: String): Result<TokenDto, DataError.Remote> {
        return httpClient.post("auth/authenticate", body = "{}", checkToken = false) {
                header("Authorization", "Bearer $googleToken")
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
    }
}


