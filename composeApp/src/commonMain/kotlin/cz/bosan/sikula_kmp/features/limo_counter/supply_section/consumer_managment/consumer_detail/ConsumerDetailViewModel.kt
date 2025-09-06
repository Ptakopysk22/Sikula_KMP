package cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.consumer_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager.ConsumerLeader
import cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager.ConsumerRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager.CreditTransaction
import cz.bosan.sikula_kmp.managers.limo_counter_manager.transaction_manager.CreditTransactionTyp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.localTimeToAmPmHour
import network.chaintech.kmp_date_time_picker.utils.now

class ConsumerDetailViewModel(
    private val leaderRepository: LeaderRepository,
    private val consumerRepository: ConsumerRepository,
    private val consumerId: Int?,
) : ViewModel() {

    private val _state = MutableStateFlow(ConsumerDetailState())
    val state: StateFlow<ConsumerDetailState> = _state

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    currentLeader = leaderRepository.getCurrentLeaderLocal(),
                    consumerId = consumerId
                )
            }
            loadConsumer(consumerId = consumerId)
            loadPotentialConsumers()
            _state.update{
                it.copy(isLoading = false)
            }
        }
    }

    fun onAction(action: ConsumerDetailAction) {
        when (action) {
            is ConsumerDetailAction.AssignConsumer -> {
                assignConsumer(action.leader)
            }

            is ConsumerDetailAction.UpdateTag -> {
                updateNFCTag(action.tag)
            }

            is ConsumerDetailAction.ChangeReadingTagState -> {
                _state.update {
                    it.copy(readingTag = action.newState)
                }
            }
        }
    }

    private suspend fun loadConsumer(consumerId: Int?) {
        //viewModelScope.launch {
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
                        consumer = null,
                        errorMessage = error.toUiText(),
                    )
                }
            }
        }
        //}
    }

    private suspend fun loadPotentialConsumers() {
        var campLeaders: List<Leader> = emptyList()
        var campConsumers: List<ConsumerLeader> = emptyList()
        var errorMassage: UiText? = null
        leaderRepository.getCampsLeaders(
            campId = state.value.currentLeader.camp.id,
            role = _state.value.currentLeader.leader.role
        ).onSuccess { leaders ->
            campLeaders = leaders
            consumerRepository.getConsumers(
                campId = state.value.currentLeader.camp.id,
            ).onSuccess { consumers ->
                campConsumers = consumers
            }.onError { error ->
                errorMassage = error.toUiText()
            }
        }.onError { error ->
            errorMassage = error.toUiText()
        }
        val potentialConsumers =
            campLeaders.filterNot { leader -> campConsumers.any { consumer -> consumer.leader.id == leader.id } }
        _state.update {
            it.copy(
                potentialConsumers = potentialConsumers,
                errorMessage = errorMassage
            )
        }
    }

    private fun assignConsumer(leader: Leader) {
        viewModelScope.launch {
            consumerRepository.assignConsumer(
                campId = _state.value.currentLeader.camp.id,
                leader = leader
            ).onSuccess {
                _state.update {
                    it.copy(consumerId = leader.id)
                }
                loadConsumer(consumerId = leader.id)
            }.onError { error ->
                _state.update {
                    it.copy(
                        consumer = null,
                        errorMessage = error.toUiText(),
                    )
                }
            }
        }
    }

    private fun updateNFCTag(tag: String?) {
        viewModelScope.launch {
            consumerRepository.updateConsumerTag(
                tag = tag,
                consumerId = _state.value.consumerId!!,
                campId = _state.value.currentLeader.camp.id
            ).onSuccess {
                _state.update {
                    it.copy(
                        consumer = _state.value.consumer?.copy(
                            consumer = _state.value.consumer?.consumer?.copy(
                                tag = tag
                            )!!
                        ), isTagExisting = false
                    )
                }
            }.onError { error ->
                if (error == DataError.Remote.UNKNOWN) {
                    _state.update {
                        it.copy(isTagExisting = true)
                    }
                } else {
                    _state.update {
                        it.copy(
                            consumer = null,
                            isTagExisting = false,
                            errorMessage = error.toUiText(),
                        )
                    }
                }
            }
        }
    }

}

sealed interface ConsumerDetailAction {
    data class AssignConsumer(val leader: Leader) : ConsumerDetailAction
    data class UpdateTag(val tag: String?) : ConsumerDetailAction
    data class ChangeReadingTagState(val newState: Boolean) : ConsumerDetailAction
}

data class ConsumerDetailState(
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val consumer: ConsumerLeader? = ConsumerLeader.EMPTY,
    val potentialConsumers: List<Leader> = emptyList(),
    val consumerId: Int? = null,
    val readingTag: Boolean = false,
    val isTagExisting: Boolean = false,
    val creditTransactions: List<CreditTransaction> = listOf(
        CreditTransaction(
            id = 1,
            price = -12.0,
            typ = CreditTransactionTyp.ITEM_PURCHASE,
            title = "Kozel 11",
            timeStamp = LocalDateTime.now(),
            consumerId = 23,
            cashierId = 15,
            comment = ""
        ),
        CreditTransaction(
            id = 2,
            price = 200.0,
            typ = CreditTransactionTyp.CREDIT_TOP_UP,
            title = "",
            timeStamp = LocalDateTime.now(),
            consumerId = 23,
            cashierId = 15,
            comment = ""
        )
    )
)