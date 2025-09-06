package cz.bosan.sikula_kmp.features.about_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.LeaderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update

class AboutAppViewModel(
    private val leaderRepository: LeaderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        AboutAppState()
    )
    val state: StateFlow<AboutAppState> = _state

    init {
        viewModelScope.launch {
            val currentLeader = leaderRepository.getCurrentLeaderLocal()
            _state.update {
                it.copy(currentLeader = currentLeader, isLoading = false)
            }
        }
    }

}

data class AboutAppState(
    val isLoading: Boolean = true,
    val currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    val developers: List<Developer> = listOf(
        Developer(
            name = "Filip Hruška",
            nickName = "Fíla",
            developerRole = DeveloperRole.PROJECT_MANAGER,
            description = "Projektový manažer, frontend vývojář"
        ),
        Developer(
            name = "Vít Dolejší",
            nickName = "Vítek",
            developerRole = DeveloperRole.BACKEND_DEVELOPER,
            description = "Beckend vývojář"
        ),
        Developer(
            name = "David Tvrdý",
            nickName = "Mazák",
            developerRole = DeveloperRole.BACKEND_DEVELOPER,
            description = "Beckend vývojář, implementace bodování"
        ),
        Developer(
            name = "Kamila Hrušková",
            nickName = "Čita",
            developerRole = DeveloperRole.UI_UX_DESIGNER,
            description = "UI-UX designér"
        ),
        Developer(
            name = "Marta Kuželková",
            nickName = "Máša",
            developerRole = DeveloperRole.REPORTING,
            description = "Reporting a analýza dat"
        ),
        Developer(
            name = "Martin Kopeček",
            nickName = "Kopec",
            developerRole = DeveloperRole.WEB_SUPPORT,
            description = "Webová podpora"
        ),
        Developer(
            name = "Jaroslav Havelík",
            nickName = "Jára",
            developerRole = DeveloperRole.FRONT_END_SENIOR,
            description = "Seniorní pdopora pro frontend"
        ),
        Developer(
            name = "Leila Tolarová",
            nickName = "Leila",
            developerRole = DeveloperRole.GRAPHIC,
            description = "Tvroba grafik"
        ),
        Developer(
            name = "Barbora Salajová",
            nickName = "Bára",
            developerRole = DeveloperRole.TESTER,
            description = "Tester"
        )
    )
)
