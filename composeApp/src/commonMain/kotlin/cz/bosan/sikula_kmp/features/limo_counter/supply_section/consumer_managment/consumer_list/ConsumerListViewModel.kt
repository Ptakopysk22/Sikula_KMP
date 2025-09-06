package cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.consumer_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager.Consumer
import cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager.ConsumerLeader
import cz.bosan.sikula_kmp.managers.limo_counter_manager.consumer_manager.ConsumerRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConsumerListViewModel(
    private val leaderRepository: LeaderRepository,
    private val consumerRepository: ConsumerRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ConsumerListState())
    val state: StateFlow<ConsumerListState> = _state

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(currentLeader = leaderRepository.getCurrentLeaderLocal())
            }
            //loadLeaders()
            loadConsumers()
            _state.update{
                it.copy(isLoading = false)
            }
        }
    }

    fun onAction(action: ConsumerListAction) {
        when (action) {
            ConsumerListAction.OnLogoutClicked -> onLogoutClicked()
            ConsumerListAction.ResetLogout -> resetLogoutSuccessful()
            is ConsumerListAction.OnConsumerSelected -> {}
        }
    }

    /*private suspend fun loadLeaders() {
        leaderRepository.getCampsLeaders(
            campId = state.value.currentLeader.camp.id,
            role = _state.value.currentLeader.leader.role
        ).onSuccess { leaders ->
            _state.update {
                it.copy(
                    leaders = leaders.sortedBy { it.nickName },
                    errorMassage = null,
                    isLoading = false
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    leaders = emptyList(),
                    errorMassage = error.toUiText(),
                    isLoading = false
                )
            }
        }
    }*/


    private suspend fun loadConsumers() {
        consumerRepository.getConsumers(
            campId = _state.value.currentLeader.camp.id,
        ).onSuccess { consumers ->
           // val leaders = _state.value.leaders
           /* val consumerLeaders: MutableList<ConsumerLeader> = mutableListOf()
            consumers.forEach { consumer ->
                consumerLeaders += ConsumerLeader(
                    consumer = consumer.consumer,
                    leader = consumer.leader
                )
            }*/
            _state.update {
                it.copy(
                    consumers = consumers.sortedBy { it.leader.nickName }
                        .sortedBy { !it.leader.isActive },
                   /* consumersLeaders = consumerLeaders.sortedBy { it.leader.nickName }
                        .sortedBy { !it.leader.isActive },*/
                )
            }
        }.onError { error ->
            _state.update {
                it.copy(
                    //consumersLeaders = emptyList(),
                    consumers = emptyList(),
                    errorMessage = error.toUiText(),
                )
            }
        }
    }

    private fun onLogoutClicked() {
        viewModelScope.launch {
            Firebase.auth.signOut()
            leaderRepository.deleteCurrentLeader()
            _state.update {
                it.copy(logoutSuccessful = true)
            }
        }
    }

    private fun resetLogoutSuccessful() {
        _state.update {
            it.copy(logoutSuccessful = false)
        }
    }

}

sealed interface ConsumerListAction {
    data object OnLogoutClicked : ConsumerListAction
    data object ResetLogout : ConsumerListAction
    data class OnConsumerSelected(val consumer: Consumer) : ConsumerListAction
}

data class ConsumerListState(
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val logoutSuccessful: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val consumers: List<ConsumerLeader> = emptyList(),
    //val leaders: List<Leader> = emptyList(),
    //val consumersLeaders: List<ConsumerLeader> = emptyList(),
)