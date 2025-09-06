package cz.bosan.sikula_kmp.managers.limo_counter_manager.product_manager

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

class ProductRepository(
    private val productDataSource: ProductDataSource
) {

    suspend fun getProducts(
        campId: Int,
    ): Result<List<Product>, DataError.Remote> {
        return Result.Success(
            listOf(
                Product(
                    id = 1,
                    name = "Kozel 11",
                    boughtAmount = 20,
                    actualAmount = 15,
                    purchasePrice = 11.5,
                    salePrice = 12.3,
                    buyerId = 23,
                    timeStamp = LocalDateTime.now(),
                    comment = ""
                ),
                Product(
                    id = 2,
                    name = "Braník 11",
                    boughtAmount = 20,
                    actualAmount = 15,
                    purchasePrice = 11.5,
                    salePrice = 12.3,
                    buyerId = 23,
                    timeStamp = LocalDateTime.now(),
                    comment = ""
                ),
                Product(
                    id = 3,
                    name = "Radegast 10",
                    boughtAmount = 20,
                    actualAmount = 15,
                    purchasePrice = 11.5,
                    salePrice = 12.3,
                    buyerId = 116,
                    timeStamp = LocalDateTime.now(),
                    comment = ""
                ),
            )
        )
    }

    suspend fun getProduct(
        campId: Int,
        productId: Int,
    ): Result<Product, DataError.Remote> {
        return Result.Success(
            Product(
                id = 1,
                name = "Kozel 11",
                boughtAmount = 20,
                actualAmount = 15,
                purchasePrice = 11.5,
                salePrice = 12.3,
                buyerId = 23,
                timeStamp = LocalDateTime.now(),
                comment = ""
            ),
        )
    }
}