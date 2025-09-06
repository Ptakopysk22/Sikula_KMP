package cz.bosan.sikula_kmp.core.data

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlin.coroutines.coroutineContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DataError.Remote> {
    val response = try {
        execute()
    } catch (e: SocketTimeoutException) {
        return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: UnresolvedAddressException) {
        return Result.Error(DataError.Remote.NO_INTERNET)
    } catch (e: IOException) {
        return Result.Error(DataError.Remote.NO_INTERNET)
    } catch (e: JsonConvertException) {
        if (e.message?.contains("Invalid refresh token or user", ignoreCase = true) == true) {
            return Result.Error(DataError.Remote.UNAUTHORIZED)
        }
        return Result.Error(DataError.Remote.UNKNOWN)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        return Result.Error(DataError.Remote.UNKNOWN)
    }
    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, DataError.Remote> {
    //val bodyAsText = response.bodyAsText()
    //val message = extractMessageFromJsonBody(bodyAsText) //TODO: pack error message with error
    return when (response.status.value) {
        in 200..209 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }
        400 -> Result.Error(DataError.Remote.BAD_REQUEST)
        401 -> Result.Error(DataError.Remote.UNAUTHORIZED)
        403 -> Result.Error(DataError.Remote.FORBIDDEN)
        404 -> Result.Error(DataError.Remote.NOT_FOUND)
        408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Remote.SERVER)
        else -> Result.Error(DataError.Remote.UNKNOWN)
    }
}

@Serializable
data class ErrorResponse(val errorMessage: String)

fun extractMessageFromJsonBody(body: String): String? {
    return try {
        Json.decodeFromString<ErrorResponse>(body).errorMessage
    } catch (e: SerializationException) {
        null
    }
}