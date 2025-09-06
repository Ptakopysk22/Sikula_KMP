package cz.bosan.sikula_kmp.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import cz.bosan.sikula_kmp.core.data.TokenHolder
import cz.bosan.sikula_kmp.core.data.TokenLogoutStateManager
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.managers.server_manager.TokenInfo
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val leaderRepository: LeaderRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AppState(isUserLoggedIn = false))
    val state: StateFlow<AppState> = _state

    init {
        GoogleAuthProvider
            .create(
                credentials = GoogleAuthCredentials(
                    serverId = "secret"
                )
            )
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            if (currentLeader != CurrentLeader.EMPTY) {
                TokenHolder.updateAll(
                    tokenInfo = currentLeader.tokenInfo,
                    email = currentLeader.leader.mail
                )
                _state.value = AppState(
                    isUserLoggedIn = true
                )
            } else {
                _state.value = AppState(isUserLoggedIn = false)
            }

            viewModelScope.launch {
                leaderRepository.getCurrentLeaderLocalFlow()
                    .collect { leader ->
                        _state.update {
                            it.copy(
                                currentLeader = leader
                            )
                        }
                    }
            }
            viewModelScope.launch {
                TokenLogoutStateManager.shouldLogout.collect { shouldLogout ->
                    if (shouldLogout) {
                        _state.update {
                            it.copy(shouldLogout = shouldLogout, isUserLoggedIn = false)
                        }
                        Firebase.auth.signOut()
                        leaderRepository.deleteCurrentLeader()
                        TokenLogoutStateManager.resetLogoutFlag()
                    }
                }
            }
        }
    }
}

data class AppState(
    val isUserLoggedIn: Boolean,
    val shouldLogout: Boolean = false,
    val tokenInfo: TokenInfo = TokenInfo.EMPTY,
    val errorMessage: UiText? = null,
    val currentLeader: CurrentLeader? = null
)
