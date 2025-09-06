package cz.bosan.sikula_kmp.features.attendee_management.leader_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import cz.bosan.sikula_kmp.core.presentation.components.BackButtonRow
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.PrimaryButton
import cz.bosan.sikula_kmp.core.presentation.components.TopBox
import cz.bosan.sikula_kmp.core.presentation.components.forms.BirthDatePicker
import cz.bosan.sikula_kmp.core.presentation.components.forms.DropDownTextField
import cz.bosan.sikula_kmp.core.presentation.components.forms.PositionCheckboxes
import cz.bosan.sikula_kmp.core.presentation.components.forms.Switcher
import cz.bosan.sikula_kmp.core.presentation.components.forms.TextField
import cz.bosan.sikula_kmp.features.attendee_management.AttendeeManagerViewModel
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.managers.leader_manager.presentation.getRoleName
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.active
import sikula_kmp.composeapp.generated.resources.email
import sikula_kmp.composeapp.generated.resources.group
import sikula_kmp.composeapp.generated.resources.name
import sikula_kmp.composeapp.generated.resources.nickname
import sikula_kmp.composeapp.generated.resources.nonactive
import sikula_kmp.composeapp.generated.resources.role
import sikula_kmp.composeapp.generated.resources.save
import sikula_kmp.composeapp.generated.resources.user_id_format
import sikula_kmp.composeapp.generated.resources.validation_error_email
import sikula_kmp.composeapp.generated.resources.validation_error_group
import sikula_kmp.composeapp.generated.resources.validation_error_name
import sikula_kmp.composeapp.generated.resources.validation_error_nickname
import sikula_kmp.composeapp.generated.resources.validation_error_role

@Composable
fun LeaderDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: LeaderDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val attendeeManagerViewModel: AttendeeManagerViewModel = getKoin().get()

    LaunchedEffect(state.isLeaderAssignSuccessfully) {
        if (state.isLeaderAssignSuccessfully) {
            attendeeManagerViewModel.deleteSelectedUser()
            onBackClick()
        }
    }

    LeaderDetailScreen(
        state = state,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                is LeaderDetailAction.OnBackClick -> {
                    attendeeManagerViewModel.deleteSelectedUser()
                    onBackClick()
                }

                else -> Unit
            }
            viewModel.onAction(action)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaderDetailScreen(
    state: LeaderDetailState,
    modifier: Modifier = Modifier,
    onAction: (LeaderDetailAction) -> Unit,
) {

    var expandRoleState by remember { mutableStateOf(false) }
    var expandGroupState by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (state.leader?.id != newItemID) {
                        TopBox(stringResource(Res.string.user_id_format, state.leader?.id.toString()))
                    }
                },
                navigationIcon = {
                    BackButtonRow(
                        onBackClick = { onAction(LeaderDetailAction.OnBackClick) },
                        keyboardController = keyboardController,
                    )
                },
                actions = {
                    PrimaryButton(
                        content = {
                            Text(
                                text = stringResource(Res.string.save),
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            onAction(LeaderDetailAction.OnSetLeaderClick)
                            keyboardController?.hide()
                        },
                        enabled = true,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.background),
            )
        }
    ) { innerPadding ->
        if (state.errorMassage != null) {
            Message(
                text = state.errorMassage.asString(),
                messageTyp = MessageTyp.ERROR,
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                state.leader?.mail?.let {
                    TextField(
                        text = it,
                        onTextChange = {
                            onAction(LeaderDetailAction.UpdateLeader { copy(mail = it) })
                            onAction(LeaderDetailAction.UpdateValidationState("email"))
                        },
                        label = stringResource(Res.string.email),
                        enabled = if (state.leader.id == newItemID) {
                            false
                        } else {
                            true
                        },
                        keyboardController = keyboardController,
                        isValid = state.isEmailValid,
                        errorMessage = stringResource(Res.string.validation_error_email)
                    )
                }
                TextField(
                    text = state.leader?.name.orEmpty(),
                    onTextChange = {
                        onAction(LeaderDetailAction.UpdateLeader { copy(name = it) })
                        onAction(LeaderDetailAction.UpdateValidationState("name"))
                    },
                    label = stringResource(Res.string.name),
                    keyboardController = keyboardController,
                    isValid = state.isNameValid,
                    errorMessage = stringResource(Res.string.validation_error_name)
                )
                TextField(
                    text = state.leader?.nickName.orEmpty(),
                    onTextChange = {
                        onAction(LeaderDetailAction.UpdateLeader { copy(nickName = it) })
                        onAction(LeaderDetailAction.UpdateValidationState("nickname"))
                    },
                    label = stringResource(Res.string.nickname),
                    keyboardController = keyboardController,
                    isValid = state.isNicknameValid,
                    errorMessage = stringResource(Res.string.validation_error_nickname)
                )
                BirthDatePicker(
                    birthDate = state.leader?.birthDate ?: LocalDate.now(),
                    showDatePicker = showDatePicker,
                    onShowDatePickerChange = { showDatePicker = !showDatePicker },
                    onUpdateClick = {
                        showDatePicker = true
                    },
                    onDateSelected = {
                        onAction(LeaderDetailAction.UpdateLeader { copy(birthDate = it) })
                        showDatePicker = false
                    }
                )
                DropDownTextField(
                    expand = expandRoleState,
                    onExpandChange = { expandRoleState = !expandRoleState },
                    text = if (state.leader?.role == null || state.leader.role == Role.NO_ROLE) {
                        ""
                    } else {
                        state.leader.role.let { getRoleName(state.leader.role) }
                    },
                    label = stringResource(Res.string.role),
                    items = Role.entries - Role.NO_ROLE - Role.SUPPLY, //excluding Supply
                    onItemClick = { selectedRole ->
                        onAction(LeaderDetailAction.UpdateLeader { copy(role = selectedRole) })
                        onAction(LeaderDetailAction.UpdateValidationState("role"))
                        expandRoleState = false
                    },
                    keyboardController = keyboardController,
                    itemToString = { getRoleName(it) },
                    isValid = state.isRoleValid,
                    errorMessage = stringResource(Res.string.validation_error_role),
                    startPadding = 15.dp,
                    endPadding = 15.dp
                )
                if (state.leader?.role == Role.CHILD_LEADER || state.leader?.role == Role.HEAD_GROUP_LEADER) {
                    DropDownTextField(
                        expand = expandGroupState,
                        onExpandChange = { expandGroupState = !expandGroupState },
                        text = state.groups.find { it.id == state.leader.groupId }?.name ?: "",
                        label = stringResource(Res.string.group),
                        items = state.groups,
                        onItemClick = { selectedGroup ->
                            onAction(LeaderDetailAction.UpdateLeader { copy(groupId = selectedGroup.id) })
                            onAction(LeaderDetailAction.UpdateValidationState("group"))
                            expandGroupState = false
                        },
                        keyboardController = keyboardController,
                        itemToString = { it.name },
                        isValid = state.isGroupValid,
                        errorMessage = stringResource(Res.string.validation_error_group),
                        startPadding = 15.dp,
                        endPadding = 15.dp
                    )
                }
                Switcher(
                    selectedButtonIndex = state.selectedActiveButtonIndex,
                    firstLabel = stringResource(Res.string.active),
                    secondLabel = stringResource(Res.string.nonactive),
                    onFirstClick = { onAction(LeaderDetailAction.UpdateLeader { copy(isActive = true) }) },
                    onSecondClick = { onAction(LeaderDetailAction.UpdateLeader { copy(isActive = false) }) },
                    modifier = Modifier.fillMaxWidth()
                )
                PositionCheckboxes(
                    leaderPositions = state.leader?.positions?.toList() ?: emptyList(),
                    role = state.leader?.role ?: Role.NO_ROLE,
                    onPositionChange = { position, isChecked ->
                        onAction(LeaderDetailAction.UpdateLeader {
                            copy(
                                positions = if (isChecked) {
                                    (positions.toList() + position).distinct()
                                } else {
                                    (positions.toList() - position)
                                }
                            )
                        }
                        )
                    }
                )
            }
        }
    }
}

