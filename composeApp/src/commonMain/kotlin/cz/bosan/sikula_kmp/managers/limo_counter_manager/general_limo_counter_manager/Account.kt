package cz.bosan.sikula_kmp.managers.limo_counter_manager.general_limo_counter_manager

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Account(
    val id: Int,
    val campId: Int,
    val name: String,
    val balance: Double,
)

@Serializable
data class AccountDto(
    @SerialName("bankId") val id: Int,
    @SerialName("campId") val campId: Int,
    @SerialName("name") val name: String,
    @SerialName("balance") val balance: Double,
)