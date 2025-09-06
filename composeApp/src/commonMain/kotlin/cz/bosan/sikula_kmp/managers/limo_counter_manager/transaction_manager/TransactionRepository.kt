package cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager

import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Result

class TransactionRepository(
    private val transactionDataSource: TransactionDataSource
) {

    suspend fun createDepositTransaction(
        campId: Int,
        newDepositTransaction: NewDepositTransaction
    ): Result<Unit, DataError.Remote> {
        return transactionDataSource.createDepositTransaction(
            campId = campId,
            newDepositTransactionDto = newDepositTransaction.toNewDepositTransactionDto()
        )
    }
}