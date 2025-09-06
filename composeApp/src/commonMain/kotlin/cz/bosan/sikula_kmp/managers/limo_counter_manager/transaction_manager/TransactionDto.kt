package cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewDepositTransactionDto(
    @SerialName("consumerId") val consumerId: Int,
    @SerialName("bankId") val accountId: Int,
    @SerialName("amount") val amount: Double,
    @SerialName("comment") val comment: String,
)