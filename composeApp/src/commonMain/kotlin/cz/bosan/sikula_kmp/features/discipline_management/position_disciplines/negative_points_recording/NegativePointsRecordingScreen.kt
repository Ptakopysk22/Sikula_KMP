package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.negative_points_recording

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.core.presentation.components.forms.Switcher
import cz.bosan.sikula_kmp.core.presentation.components.forms.SwitcherButton
import cz.bosan.sikula_kmp.core.presentation.components.forms.TextField
import cz.bosan.sikula_kmp.features.attendee_management.check_user.SearchBar
import cz.bosan.sikula_kmp.managers.children_manager.domain.ChildRole
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.nickname
import sikula_kmp.composeapp.generated.resources.reason
import sikula_kmp.composeapp.generated.resources.validation_error_comment

@Composable
fun NegativePointsRecordingRoute(
    modifier: Modifier = Modifier,
    viewModel: NegativePointsRecordingViewModel = koinViewModel(),
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NegativePointsRecordingScreen(
        state = state,
        modifier = modifier,
        onBackClick = onBackClick,
        onAction = viewModel::onAction
    )
}

@Composable
private fun NegativePointsRecordingScreen(
    state: NegativePointsRecordingState,
    modifier: Modifier = Modifier,
    onAction: (NegativePointsRecordingAction) -> Unit,
    onBackClick: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val childrenListState = rememberLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NegativePointsRecordingTopBar(
                onBackClick = onBackClick,
                keyboardController = keyboardController,
                showSaveButton = !state.isSearchingChild,
                enabledSaveButton = state.comment.isNotEmpty(),
                onSaveClick = { onAction(NegativePointsRecordingAction.OnCreateRecord) },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WrapBox(
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                content = {
                    if (state.isSearchingChild) {
                        SearchBar(
                            searchQuery = state.searchedQuery,
                            onSearchQueryChange = {
                                onAction(
                                    NegativePointsRecordingAction.OnSearchQueryChange(
                                        it
                                    )
                                )
                            },
                            onImeSearch = {
                                keyboardController?.hide()
                                onAction(NegativePointsRecordingAction.OnSearchClick(state.searchedQuery))
                            },
                            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            search = {
                                onAction(NegativePointsRecordingAction.OnSearchClick(state.searchedQuery))
                                keyboardController?.hide()
                            },
                            label = stringResource(Res.string.nickname),
                            onIsFocusedChanged = {
                                onAction(
                                    NegativePointsRecordingAction.OnSearchBarFocusedChange(
                                        it
                                    )
                                )
                            }
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            if (state.warningMessage != null) {
                                Message(
                                    text = state.warningMessage.asString(),
                                    messageTyp = MessageTyp.WARNING
                                )
                            } else {
                                NegativePointsChildrenList(
                                    children = state.filteredChildren,
                                    onChildClick = {
                                        onAction(
                                            NegativePointsRecordingAction.OnChildSelected(
                                                it
                                            )
                                        )
                                    },
                                    crews = state.crews,
                                    groups = state.groups,
                                    scrollState = childrenListState
                                )
                            }
                        }
                    } else {
                        NegativePointsChildListItem(
                            child = state.selectedChild!!,
                            groups = state.groups,
                            crews = state.crews,
                            onClick = {},
                            modifier = Modifier.padding(
                                start = 20.dp,
                                end = 20.dp,
                                bottom = 8.dp
                            )
                        )
                        if (state.selectedChild.role == ChildRole.CREW_MASTER) {
                            Switcher(
                                selectedButtonIndex = (state.selectedNegativePointsVariant?.id!! - 2),
                                firstLabel = "${negativePointsVariantList[1].name} (${negativePointsVariantList[1].value})",
                                secondLabel = "${negativePointsVariantList[2].name} (${negativePointsVariantList[2].value})",
                                onFirstClick = {
                                    onAction(
                                        NegativePointsRecordingAction.OnNegativePointsVariantSelected(
                                            negativePointsVariant = negativePointsVariantList[1]
                                        )
                                    )
                                },
                                onSecondClick = {
                                    onAction(
                                        NegativePointsRecordingAction.OnNegativePointsVariantSelected(
                                            negativePointsVariant = negativePointsVariantList[2]
                                        )
                                    )
                                },
                                arrangement = Arrangement.Center,
                            )
                        } else {
                            SwitcherButton(
                                text = "${negativePointsVariantList[0].name} (${negativePointsVariantList[0].value})",
                                onClick = {},
                                isSelected = true,
                                width = 100.dp,
                            )
                        }
                        TextField(
                            text = state.comment,
                            onTextChange = {
                                onAction(
                                    NegativePointsRecordingAction.OnCommentChange(
                                        it
                                    )
                                )
                            },
                            label = stringResource(Res.string.reason),
                            keyboardController = keyboardController,
                            isValid = state.isCommentValid,
                            errorMessage = stringResource(Res.string.validation_error_comment),
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )

                    }
                }
            )
        }
    }
}

