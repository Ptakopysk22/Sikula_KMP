package cz.bosan.sikula_kmp.managers.limo_counter_manager.general_limo_counter_manager

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result

class GeneralLimoCounterDataSource(private val httpClient: TokenAwareHttpClient) {

    suspend fun getBankAccounts(campId: Int): Result<List<AccountDto>, DataError.Remote>{
        return httpClient.get("get-banks") {
            url { parameters.append("campID", campId.toString()) }
        }
    }
}