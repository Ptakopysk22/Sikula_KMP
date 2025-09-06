package cz.bosan.sikula_kmp.core.data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TokenLogoutStateManager {
    private val _shouldLogout = MutableStateFlow(false)
    val shouldLogout: StateFlow<Boolean> = _shouldLogout.asStateFlow()

    fun triggerLogout() {
        _shouldLogout.value = true
    }

    fun resetLogoutFlag() {
        _shouldLogout.value = false
    }
}