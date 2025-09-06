package cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.edit_deposit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager.ConsumerLeader
import cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager.ConsumerRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.general_limo_counter_manager.Account
import cz.bosan.sikula_kmp.managers.limo_counter_manager.general_limo_counter_manager.GeneralLimoCounterRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager.NewDepositTransaction
import cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditDepositViewModel(
    private val leaderRepository: LeaderRepository,
    private val consumerRepository: ConsumerRepository,
    private val generalLimoCounterRepository: GeneralLimoCounterRepository,
    private val transactionRepository: TransactionRepository,
    private val consumerId: Int?,
) : ViewModel() {

    private val _state = MutableStateFlow(EditDepositState())
    val state: StateFlow<EditDepositState> = _state

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                    consumerId = consumerId
                )
            }
            loadConsumer()
            loadBankAccounts()
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun onAction(action: EditDepositAction) {
        when (action) {
            is EditDepositAction.CreateDepositRecord -> createDepositTransaction()
            is EditDepositAction.ChangeMoneyAmount -> {
                _state.update {
                    it.copy(moneyAmount = action.moneyAmount)
                }
                viewModelScope.launch {
                    createQrCod()
                }
            }

            is EditDepositAction.OnAccountChange -> {
                _state.update {
                    it.copy(selectedAccountIndex = action.index)
                }
                viewModelScope.launch {
                    createQrCod()
                }
            }

            is EditDepositAction.ChangeComment -> {
                _state.update {
                    it.copy(comment = action.comment)
                }
            }
        }
    }

    private suspend fun loadConsumer() {
        if (consumerId != null) {
            val campId = _state.value.currentLeader.camp.id
            consumerRepository.getConsumerLeader(
                consumerId = consumerId,
                campId = campId
            ).onSuccess { consumerLeader ->
                _state.update {
                    it.copy(
                        consumer = consumerLeader,
                    )
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        consumer = ConsumerLeader.EMPTY,
                        errorMessage = error.toUiText(),
                    )
                }
            }
        }
    }

    private suspend fun loadBankAccounts() {
        generalLimoCounterRepository.getBankAccounts(campId = _state.value.currentLeader.camp.id)
            .onSuccess { accounts ->
                val updatedAccounts: MutableList<Account> = mutableListOf()
                for (account in accounts) {
                    if (account.name.contains("Cash") || account.name.contains("Hotovost")) {
                        updatedAccounts += account.copy(name = "Hotovost")
                    } else {
                        updatedAccounts += account.copy(name = "Bankovní účet")
                    }
                    updatedAccounts.sortByDescending { it.name }
                }
                _state.update {
                    it.copy(accounts = updatedAccounts,)
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        accounts = emptyList(),
                        errorMessage = error.toUiText(),
                    )
                }
            }
    }

    private fun createDepositTransaction() {
        viewModelScope.launch {
            val amount = _state.value.moneyAmount?.trim()?.replace(",", ".")?.toDouble()
            transactionRepository.createDepositTransaction(
                campId = _state.value.currentLeader.camp.id,
                newDepositTransaction = NewDepositTransaction(
                    consumerId = _state.value.consumer.consumer.consumerId,
                    accountId = _state.value.accounts[_state.value.selectedAccountIndex].id,
                    amount = amount ?: 0.0,
                    comment = _state.value.comment
                )
            ).onSuccess {
                _state.update{
                    it.copy(savedSuccessfully = true)
                }
            }.onError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.toUiText(),
                    )
                }
            }
        }
    }

    private suspend fun createQrCod() {
        var qrStateMessage: UiText? = null
        var qrCode: ByteArray? = null
        var enabledSave: Boolean = false
        val amount = try {
            val amount = _state.value.moneyAmount?.trim()?.replace(",", ".")
            amount?.toDouble()
        } catch (e: Exception) {
            null
        }
        if (amount != null) {
            enabledSave = true
        }
        if (_state.value.selectedAccountIndex == 0) {
            qrStateMessage = Warning.QrState.PAYMENT_IN_CASH.toUiText()
        } else {
            if (amount == null) {
                qrStateMessage = Warning.QrState.NOT_VALID_AMOUNT.toUiText()
            } else {
                if (amount > 0 && _state.value.currentLeader.leader.bankAccount == null) {
                    qrStateMessage = Warning.QrState.NOT_FILL_SUPPLIER_BANK_ACCOUNT.toUiText()
                } else if (amount <= 0 && _state.value.consumer.leader.bankAccount == null) {
                    qrStateMessage = Warning.QrState.NOT_FILL_CONSUMER_BANK_ACCOUNT.toUiText()
                } else {
                    var bankAccount = ""
                    var message = ""
                    if (amount > 0) {
                        bankAccount = _state.value.currentLeader.leader.bankAccount!!
                        message =
                            "Dobití kreditu na čárkovník - ${_state.value.consumer.leader.nickName} (userId: ${_state.value.consumer.leader.id})"
                        qrStateMessage = Warning.QrState.PAYMENT_BY_CONSUMER.toUiText()
                    } else if (amount <= 0) {
                        bankAccount = _state.value.consumer.leader.bankAccount!!
                        message =
                            "Vratka přeplatku z čárkovníku. Zaúčtoval: ${_state.value.currentLeader.leader.nickName}"
                        qrStateMessage = Warning.QrState.PAYMENT_BY_SUPPLIER.toUiText()
                    }
                    generalLimoCounterRepository.downloadQrCode(
                        iban = bankAccount,
                        amount = amount,
                        message = message
                    ).onSuccess { QR ->
                        qrCode = QR
                    }.onError { qrStateMessage = Warning.QrState.QR_GENERATING_ERROR.toUiText() }
                }
            }
        }
        _state.update {
            it.copy(
                qrCodStateMessage = qrStateMessage, qrCode = qrCode, enabledSave = enabledSave
            )
        }
    }


}

sealed interface EditDepositAction {
    data object CreateDepositRecord : EditDepositAction
    data class ChangeMoneyAmount(val moneyAmount: String) : EditDepositAction
    data class ChangeComment(val comment: String) : EditDepositAction
    data class OnAccountChange(val index: Int) : EditDepositAction


}

data class EditDepositState(
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val consumer: ConsumerLeader = ConsumerLeader.EMPTY,
    val consumerId: Int? = null,
    val moneyAmount: String? = null,
    val comment: String = "",
    val accounts: List<Account> = emptyList(),
    val selectedAccountIndex: Int = 0,
    val qrCode: ByteArray? = null,
    val qrCodStateMessage: UiText? = Warning.QrState.PAYMENT_IN_CASH.toUiText(),
    val enabledSave: Boolean = false,
    val savedSuccessfully: Boolean = false
)