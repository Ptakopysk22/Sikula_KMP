package cz.bosan.sikula_kmp.managers.limo_counter_manager.product_manager

import cz.bosan.sikula_kmp.core.domain.newItemID
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

data class Product(
    val id: Int,
    val name: String,
    //val typ:
    val boughtAmount: Int?,
    val actualAmount: Int?,
    val purchasePrice: Double?,
    val salePrice: Double?,
    val buyerId: Int,
    val timeStamp: LocalDateTime,
    val comment: String
) {
    companion object {
        val EMPTY = Product(
            id = newItemID,
            name = "",
            boughtAmount = null,
            actualAmount = null,
            purchasePrice = null,
            salePrice = null,
            buyerId = 0,
            timeStamp = LocalDateTime.now(),
            comment = ""
        )
    }
}
