package cz.bosan.sikula_kmp.core.data

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class TokenAwareHttpClient(
    @PublishedApi internal val httpClient: HttpClient,
    private val tokenManagerLazy: Lazy<TokenManager>,
) {
    private val tokenManager get() = tokenManagerLazy.value

    suspend inline fun <reified T> get(
        url: String,
        checkToken: Boolean = true,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ):  Result<T, DataError.Remote> {
        if (checkToken) ensureTokenValid()
        return safeCall<T> {
            httpClient.get(url, block)
        }
    }

    suspend inline fun <reified T> post(
        url: String,
        body: Any? = null,
        checkToken: Boolean = true,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Result<T, DataError.Remote>  {
        if (checkToken) ensureTokenValid()
        return safeCall<T> {
            httpClient.post(url) {
                if (body != null) setBody(body)
                apply(block)
            }
        }
    }

    suspend inline fun <reified T> put(
        url: String,
        body: Any? = null,
        checkToken: Boolean = true,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Result<T, DataError.Remote> {
        if (checkToken) ensureTokenValid()
        return safeCall<T> {
            httpClient.put(url) {
                if (body != null) setBody(body)
                apply(block)
            }
        }
    }

    suspend fun ensureTokenValid() {
        if (!tokenManager.isTokenValid()) {
            val success = tokenManager.refreshTokenIfNeeded()
            if (!success) {
                TokenLogoutStateManager.triggerLogout()
            }
        }
    }
}