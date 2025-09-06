package cz.bosan.sikula_kmp.features.points_management.crew_points

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.core.domain.Destination
import cz.bosan.sikula_kmp.core.presentation.components.CampDayCarousel
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.NavigationBar
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.features.points_management.components.PointsTopBar
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CrewPointsRoute(
    onRecordClick: (Discipline, Crew) -> Unit,
    onBackClick: () -> Unit,
    navigationBarActions: NavigationBarActions,
    modifier: Modifier = Modifier,
    viewModel: CrewPointsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CrewPointsScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        navigationBarActions = navigationBarActions,
        onRecordClick = onRecordClick,
        onBackClick = onBackClick,
    )
}

@Composable
private fun CrewPointsScreen(
    state: CrewPointsState,
    modifier: Modifier = Modifier,
    onAction: (CrewPointsAction) -> Unit,
    navigationBarActions: NavigationBarActions,
    onRecordClick: (Discipline, Crew) -> Unit,
    onBackClick: () -> Unit,
) {
    val pagerState = rememberPagerState { state.campDuration }
    val keyboardController = LocalSoftwareKeyboardController.current

    val renderWithoutAnimation = remember { mutableStateOf(true) }

    LaunchedEffect(state.campDay) {
        val targetPage = state.campDay - 1
        if (pagerState.currentPage != targetPage) {
            if (renderWithoutAnimation.value) {
                pagerState.scrollToPage(targetPage)
                renderWithoutAnimation.value = false
            } else {
                pagerState.animateScrollToPage(targetPage)
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (!state.isInitialized) return@LaunchedEffect

        val newDay = pagerState.currentPage + 1
        if (newDay != state.campDay) {
            onAction(CrewPointsAction.OnChangeCampDay(newDay))
        }
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            val targetPage = pagerState.currentPage.coerceIn(0, state.campDuration - 1)
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =
        {
            PointsTopBar(
                onBackClick = { onBackClick() },
                discipline = null,
                crew = state.crew,
                keyboardController = keyboardController,
            )
        },
        bottomBar = {
            NavigationBar(
                role = state.currentLeader.leader.role,
                currentDestination = Destination.POINTS_MANAGER,
                navigationBarActions = navigationBarActions
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            CampDayCarousel(
                campDay = state.campDay,
                campDuration = state.campDuration,
                changeWithoutAnimation = { renderWithoutAnimation.value = true },
                onDayChanged = { campDay ->
                    onAction(
                        CrewPointsAction.OnChangeCampDay(
                            campDay
                        )
                    )
                },
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f),
                userScrollEnabled = (pagerState.currentPage in 0..<state.campDuration)
            ) { pageIndex ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(vertical = 4.dp),
                    contentAlignment = Alignment.TopStart
                )
                {
                    WrapBox(
                        isLoading = state.isLoading,
                        errorMessage = state.errorMessage,
                        content = {
                            Column {
                                if (state.warningMessage != null) {
                                    Message(
                                        text = state.warningMessage.asString(),
                                        messageTyp = MessageTyp.WARNING
                                    )
                                } else {
                                    PointRecordListCrew(
                                        records = state.records,
                                        onRecordClick = {
                                            onRecordClick(
                                                it,
                                                state.crew,
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
