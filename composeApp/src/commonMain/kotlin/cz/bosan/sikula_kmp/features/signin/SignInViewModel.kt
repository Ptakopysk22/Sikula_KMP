package cz.bosan.sikula_kmp.features.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpauth.google.GoogleUser
import cz.bosan.sikula_kmp.core.data.TokenHolder
import cz.bosan.sikula_kmp.core.domain.DataError
import cz.bosan.sikula_kmp.core.domain.Warning
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Camp
import cz.bosan.sikula_kmp.managers.camp_manager.domain.CampRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Leader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.core.domain.onError
import cz.bosan.sikula_kmp.core.domain.onSuccess
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.toUiText
import cz.bosan.sikula_kmp.managers.server_manager.ServerRepository
import cz.bosan.sikula_kmp.managers.server_manager.TokenInfo
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val leaderRepository: LeaderRepository,
    private val campRepository: CampRepository,
    private val serverRepository: ServerRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state

    private var camps: List<Camp> = emptyList()
    private var leader: Leader? = null

    init {
        viewModelScope.launch {
            campRepository.getCamps()
                .onSuccess { result ->
                    camps = result
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            errorMessage = error.toUiText(),
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun onAction(action: SignInAction) {
        when (action) {
            is SignInAction.OnSignInResult -> onSignInResult(action.result)
            is SignInAction.ResetSignInResult -> resetSignInResult()
            is SignInAction.OnCampSelected -> {
                viewModelScope.launch { setCurrentLeadersCamp(action.camp, _state.value.tokenInfo) }
            }
        }
    }

    private suspend fun setCurrentLeadersCamp(camp: Camp, tokenInfo: TokenInfo) {
        _state.update {
            it.copy(isLoading = true)
        }
        val currentLeader = CurrentLeader(
            leader = leader!!,
            camp = camp,
            imageUrl = Firebase.auth.currentUser?.photoURL,
            tokenInfo = tokenInfo
        )
        leaderRepository.setCurrentLeader(currentLeader)
        TokenHolder.updateAll(tokenInfo = tokenInfo, email = _state.value.signedUser?.email)
        _state.update {
            it.copy(
                campSelected = true,
            )
        }
    }

    private fun onSignInResult(result: GoogleUser?) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            result?.let { googleUser ->
                //	desktop limitations https://github.com/GitLiveApp/firebase-java-sdk?tab=readme-ov-file#limitations :(
                try {
                    val credential =
                        GoogleAuthProvider.credential(googleUser.idToken, googleUser.accessToken)
                    firebaseAuth.signInWithCredential(credential)
                    _state.update {
                        it.copy(signedUser = googleUser)
                    }
                    serverRepository.getBackendToken(googleToken = googleUser.idToken)
                        .onSuccess { tokenInfo ->
                            _state.update {
                                it.copy(tokenInfo = tokenInfo)
                            }
                            TokenHolder.updateAll(
                                tokenInfo = tokenInfo,
                                email = _state.value.signedUser?.email
                            )
                            leaderRepository.getCurrentLeaderRemote(firebaseAuth.currentUser?.email.orEmpty())
                                .onSuccess { remoteLeader ->
                                    leader = remoteLeader
                                    if (remoteLeader.occupations.size > 1) {
                                        setCampsRoles(remoteLeader)
                                        _state.update {
                                            it.copy(multipleCampsChoose = true, isLoading = false)
                                        }
                                    } else {
                                        camps.find { it.id == remoteLeader.occupations[0].campId }
                                            ?.let {
                                                setCurrentLeadersCamp(
                                                    camp = it,
                                                    _state.value.tokenInfo
                                                )
                                            }
                                    }
                                }.onError { error ->
                                    if (error == DataError.Remote.NOT_FOUND) {
                                        _state.update {
                                            it.copy(
                                                warningMessage = Warning.Common.NO_CAMP.toUiText(),
                                                isLoading = false
                                            )
                                        }
                                    } else {
                                        _state.update {
                                            it.copy(
                                                errorMessage = error.toUiText(),
                                                isLoading = false
                                            )
                                        }
                                    }
                                    Firebase.auth.signOut()
                                }
                        }
                        .onError { error ->
                            _state.update {
                                it.copy(errorMessage = error.toUiText(), isLoading = false)
                            }
                        }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            errorMessage = DataError.Remote.NO_INTERNET.toUiText(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun setCampsRoles(leader: Leader) {
        val campsRoles = leader.occupations.mapNotNull { occupation ->
            val camp = camps.find { it.id == occupation.campId }
            camp?.let { it to occupation.role }
        }
        _state.update {
            it.copy(
                campsRoles = campsRoles
            )
        }
    }

    private fun resetSignInResult() {
        _state.update {
            it.copy(signedUser = null)
        }
    }
}

sealed interface SignInAction {
    data class OnSignInResult(val result: GoogleUser?) : SignInAction
    data object ResetSignInResult : SignInAction
    data class OnCampSelected(val camp: Camp) : SignInAction
}

data class SignInState(
    val isLoading: Boolean = true,
    val signedUser: GoogleUser? = null,
    val tokenInfo: TokenInfo = TokenInfo.EMPTY,
    val multipleCampsChoose: Boolean = false,
    val campSelected: Boolean = false,
    val errorMessage: UiText? = null,
    val warningMessage: UiText? = null,
    val campsRoles: List<Pair<Camp, Role>> = emptyList()
)