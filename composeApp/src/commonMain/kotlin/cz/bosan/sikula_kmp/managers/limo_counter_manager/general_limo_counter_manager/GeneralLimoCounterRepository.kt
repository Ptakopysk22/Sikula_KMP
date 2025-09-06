package cz.bosan.sikula_kmp.managers.limo_counter_manager.general_limo_counter_manager

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.core.domain.map

class GeneralLimoCounterRepository(
    private val externalLimoCounterDataSource: ExternalLimoCounterDataSource,
    private val generalLimoCounterDataSource: GeneralLimoCounterDataSource
) {

    suspend fun downloadQrCode(
        iban: String,
        amount: Double,
        message: String
    ): Result<ByteArray, DataError.Remote> {
        return externalLimoCounterDataSource.downloadQrCode(
            iban = iban,
            amount = amount,
            message = message
        )
    }

    suspend fun getBankAccounts(campId: Int): Result<List<Account>, DataError.Remote> {
        return generalLimoCounterDataSource.getBankAccounts(campId = campId)
            .map { dto -> dto.map { it.toAccount() } }
    }

}