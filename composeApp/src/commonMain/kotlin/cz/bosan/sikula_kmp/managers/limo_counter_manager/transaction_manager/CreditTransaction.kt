package cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager

import kotlinx.datetime.LocalDateTime

data class CreditTransaction(
    val id: Int,
    val price: Double,
    val typ: CreditTransactionTyp,
    val title: String,
    val timeStamp: LocalDateTime,
    val consumerId: Int,
    val cashierId: Int,
    val comment: String
)

enum class CreditTransactionTyp {
    CREDIT_TOP_UP,
    CREDIT_REFUND,
    ITEM_PURCHASE
}
