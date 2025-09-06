package cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager

data class NewDepositTransaction(
    val consumerId: Int,
    val accountId: Int,
    val amount: Double,
    val comment: String
)