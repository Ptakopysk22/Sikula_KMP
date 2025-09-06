package cz.bosan.sikula_kmp.managers.limo_counter_manager.product_manager

import cz.bosan.sikula_kmp.core.data.TokenAwareHttpClient
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager.ConsumerLeaderDto

class ProductDataSource(
    private val httpClient: TokenAwareHttpClient
) {
    suspend fun getProducts(campId: Int): Result<List<ConsumerLeaderDto>, DataError.Remote> {
        return httpClient.get("get-all-camp-consumers") {
            url { parameters.append("campID", campId.toString()) }
        }
    }
}