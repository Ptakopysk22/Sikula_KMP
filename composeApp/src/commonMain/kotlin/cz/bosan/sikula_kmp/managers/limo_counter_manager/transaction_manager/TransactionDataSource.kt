package cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TransactionDataSource(
    private val httpClient: TokenAwareHttpClient
) {
    suspend fun createDepositTransaction(
        campId: Int,
        newDepositTransactionDto: NewDepositTransactionDto,
    ): Result<Unit, DataError.Remote> {
        return httpClient.post("create-deposit-transaction") {
            url { parameters.append("campID", campId.toString()) }
            contentType(ContentType.Application.Json)
            setBody(newDepositTransactionDto)

        }
    }
}