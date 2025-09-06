package cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager

fun NewDepositTransaction.toNewDepositTransactionDto(): NewDepositTransactionDto {
    return NewDepositTransactionDto(
        consumerId = consumerId,
        accountId = accountId,
        amount = amount,
        comment = comment
    )
}