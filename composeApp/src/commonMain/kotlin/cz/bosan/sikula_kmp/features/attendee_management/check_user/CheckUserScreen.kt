package cz.bosan.sikula_kmp.features.attendee_management.check_user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.core.domain.newItemID
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.forms.Switcher
import cz.bosan.sikula_kmp.managers.user_manager.User
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_create_new_account
import sikula_kmp.composeapp.generated.resources.description_search_child
import sikula_kmp.composeapp.generated.resources.description_search_leader
import sikula_kmp.composeapp.generated.resources.email
import sikula_kmp.composeapp.generated.resources.nickname

@Composable
fun CheckUserRoute(
    modifier: Modifier = Modifier,
    viewModel: CheckUserViewModel = koinViewModel(),
    onAssignAttendee: (User) -> Unit,
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CheckUserScreen(
        state = state,
        modifier = modifier,
        onBackClick = onBackClick,
        onAction = { action ->
            when (action) {
                is CheckUserAction.OnUserSelected -> onAssignAttendee(action.user)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun CheckUserScreen(
    state: CheckUserState,
    modifier: Modifier = Modifier,
    onAction: (CheckUserAction) -> Unit,
    onBackClick: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val searchParameters =
        listOf(stringResource(Res.string.nickname), stringResource(Res.string.email))
    val userListState = rememberLazyListState()
    var showInfo by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CheckUserTopBar(
                onBackClick = onBackClick,
                showInfoChange = { showInfo = !showInfo },
                keyboardController = keyboardController,
                isCheckingLeader = state.isCheckingLeader
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if(showInfo){
                Message(
                    text = if(state.isCheckingLeader) stringResource(Res.string.description_search_leader) else stringResource(Res.string.description_search_child),
                    messageTyp = MessageTyp.INFO,
                )
            }
            if (state.isCheckingLeader) {
                Switcher(
                    selectedButtonIndex = state.selectedParameterIndex,
                    firstLabel = searchParameters[0],
                    secondLabel = searchParameters[1],
                    onFirstClick = { onAction(CheckUserAction.OnParameterSelected(index = 0)) },
                    onSecondClick = { onAction(CheckUserAction.OnParameterSelected(index = 1)) },
                    arrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                )
            }
            SearchBar(
                searchQuery = state.searchedQuery,
                onSearchQueryChange = { onAction(CheckUserAction.OnSearchQueryChange(it)) },
                onImeSearch = {
                    keyboardController?.hide()
                    onAction(CheckUserAction.OnSearchClick(state.searchedQuery))
                },
                modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
                    .padding(horizontal = 20.dp),
                search = {
                    onAction(CheckUserAction.OnSearchClick(state.searchedQuery))
                    keyboardController?.hide()
                },
                label = searchParameters[state.selectedParameterIndex],
                onIsFocusedChanged = { onAction(CheckUserAction.OnSearchBarFocusedChange(it)) }
            )
            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (state.warningMessage != null) {
                    Message(text = state.warningMessage.asString(), messageTyp = MessageTyp.WARNING)
                } else {
                    UserList(
                        users = state.searchedUsers,
                        onUserClick = {
                            onAction(CheckUserAction.OnUserSelected(it))
                            keyboardController?.hide()
                        },
                        scrollState = userListState,
                        onlyChildren = !state.isCheckingLeader
                    )
                }
            }
            if (state.showAssignButton && state.searchedQuery.isNotBlank()) {
                ElevatedButton(
                    onClick = {
                        onAction(
                            CheckUserAction.OnUserSelected(
                                User(
                                    id = newItemID,
                                    email = if (state.isCheckingLeader) {
                                        state.searchedQuery
                                    } else {
                                        null
                                    },
                                    name = if (state.isCheckingLeader) {
                                        null
                                    } else {
                                        state.searchedQuery
                                    },
                                    nickName = null,
                                    birthDate = null,
                                )
                            )
                        )
                        keyboardController?.hide()
                    },
                    modifier = modifier.padding(
                        start = 10.dp,
                        top = 5.dp,
                        end = 10.dp,
                        bottom = if (state.isSearchBarFocused) 340.dp else 120.dp
                    ).height(37.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        stringResource(Res.string.description_create_new_account),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}
