package cz.bosan.sikula_kmp.managers.limo_counter_manager.general_limo_counter_manager

import cz.bosan.sikula_kmp.core.data.safeCall
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.encodeURLParameter

class ExternalLimoCounterDataSource(
    private val httpClient: HttpClient
) {
    suspend fun downloadQrCode(
        iban: String,
        amount: Double,
        message: String
    ): Result<ByteArray, DataError.Remote> {
        val qrData = "SPD*1.0*ACC:$iban*AM:$amount*MSG:$message"
        val encodedData = qrData.encodeURLParameter()
        val url = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=$encodedData"

        return safeCall<ByteArray> {
            httpClient.get(url)
        }
    }
}
