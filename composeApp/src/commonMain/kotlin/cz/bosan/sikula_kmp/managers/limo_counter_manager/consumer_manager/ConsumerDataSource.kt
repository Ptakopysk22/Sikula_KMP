package cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ConsumerDataSource(
    private val httpClient: TokenAwareHttpClient
) {
    suspend fun getConsumers(campId: Int): Result<List<ConsumerLeaderDto>, DataError.Remote> {
        return httpClient.get("get-all-camp-consumers") {
            url { parameters.append("campID", campId.toString()) }
        }
    }

    suspend fun getConsumer(campId: Int, consumerId: Int): Result<ConsumerLeaderDto, DataError.Remote> {
        return httpClient.get("get-consumer-by-id") {
            url {
                parameters.append("campID", campId.toString())
                parameters.append("consumerId", consumerId.toString())
            }
        }
    }

    suspend fun assignConsumer(
        campId: Int,
        userId: Int,
    ): Result<Unit, DataError.Remote> {
        val requestBody = AssignConsumerRequest(
            userId = userId,
            campId = campId,
            credit = 0.0,
            tag = null,
            targetBankTyp = 1,
        )
        return httpClient.post("create-consumer") {
            url { parameters.append("campID", campId.toString()) }
            contentType(ContentType.Application.Json)
            setBody(requestBody)

        }
    }

    suspend fun updateConsumerTag(
        campId: Int,
        userId: Int,
        tag: String?
    ): Result<Unit, DataError.Remote> {
        val requestBody = AssignConsumerRequest(
            userId = userId,
            campId = campId,
            credit = 0.0,
            tag = tag,
            targetBankTyp = 1,
        )
        return httpClient.put("update-consumer-nfc") {
            url { parameters.append("campID", campId.toString()) }
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }
    }


}