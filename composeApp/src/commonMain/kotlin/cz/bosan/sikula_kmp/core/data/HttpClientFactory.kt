package cz.bosan.sikula_kmp.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

object HttpClientFactory {

    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )

            }
            install(HttpTimeout) {
                socketTimeoutMillis = 5_000L
                requestTimeoutMillis = 5_000L
            }
            /*install(Logging) {
                logger = object : Logger { //delete before production
                    override fun log(message: String) {
                        // FIXME: add Napier: https://github.com/AAkira/Napier
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }*/
            defaultRequest {
                contentType(ContentType.Application.Json)
                url(BASE_URL)
                if (!headers.contains(HttpHeaders.Authorization)) {
                    TokenHolder.backendToken?.let {
                        header(HttpHeaders.Authorization, "Bearer $it")
                    }
                }
            }
        }
    }
}