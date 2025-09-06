package cz.bosan.sikula_kmp.features.attendee_management.child_detail

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
import cz.bosan.sikula_kmp.core.presentation.components.forms.Switcher
import cz.bosan.sikula_kmp.core.presentation.components.forms.TextField
import cz.bosan.sikula_kmp.features.attendee_management.AttendeeManagerViewModel
import cz.bosan.sikula_kmp.managers.children_manager.domain.ChildRole
import cz.bosan.sikula_kmp.managers.children_manager.presentation.getChildRoleName
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.active
import sikula_kmp.composeapp.generated.resources.crew
import sikula_kmp.composeapp.generated.resources.name
import sikula_kmp.composeapp.generated.resources.nickname
import sikula_kmp.composeapp.generated.resources.nonactive
import sikula_kmp.composeapp.generated.resources.role
import sikula_kmp.composeapp.generated.resources.save
import sikula_kmp.composeapp.generated.resources.trail_categories
import sikula_kmp.composeapp.generated.resources.user_id_format
import sikula_kmp.composeapp.generated.resources.validation_error_crew
import sikula_kmp.composeapp.generated.resources.validation_error_name
import sikula_kmp.composeapp.generated.resources.validation_error_nickname
import sikula_kmp.composeapp.generated.resources.validation_error_role
import sikula_kmp.composeapp.generated.resources.validation_error_trail_category

@Composable
fun ChildDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: ChildDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val attendeeManagerViewModel: AttendeeManagerViewModel = getKoin().get()

    LaunchedEffect(state.isChildAssignSuccessfully) {
        if (state.isChildAssignSuccessfully) {
            attendeeManagerViewModel.deleteSelectedUser()
            onBackClick()
        }
    }

    ChildDetailScreen(
        state = state,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                is ChildDetailAction.OnBackClick -> {
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
private fun ChildDetailScreen(
    state: ChildDetailState,
    modifier: Modifier = Modifier,
    onAction: (ChildDetailAction) -> Unit,
) {

    var expandRoleState by remember { mutableStateOf(false) }
    var expandCrewState by remember { mutableStateOf(false) }
    var expandCategoryState by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (state.child?.id != newItemID) {
                        TopBox(stringResource(Res.string.user_id_format, state.child?.id.toString()))
                    }
                },
                navigationIcon = {
                    BackButtonRow(
                        onBackClick = { onAction(ChildDetailAction.OnBackClick) },
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
                            onAction(ChildDetailAction.OnSetChildClick)
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
                TextField(
                    text = state.child?.name.orEmpty(),
                    onTextChange = {
                        onAction(ChildDetailAction.UpdateChild { copy(name = it) })
                        onAction(ChildDetailAction.UpdateValidationState("name"))
                    },
                    label = stringResource(Res.string.name),
                    keyboardController = keyboardController,
                    enabled =
                    if (state.child?.id == 9999) {
                        false
                    } else {
                        true
                    },
                    isValid = state.isNameValid,
                    errorMessage = stringResource(Res.string.validation_error_name)
                )
                TextField(
                    text = state.child?.nickName.orEmpty(),
                    onTextChange = {
                        onAction(ChildDetailAction.UpdateChild { copy(nickName = it) })
                        onAction(ChildDetailAction.UpdateValidationState("nickname"))
                    },
                    label = stringResource(Res.string.nickname),
                    keyboardController = keyboardController,
                    isValid = state.isNicknameValid,
                    errorMessage = stringResource(Res.string.validation_error_nickname)
                )
                BirthDatePicker(
                    birthDate = state.child?.birthDate ?: LocalDate.now(),
                    showDatePicker = showDatePicker,
                    onShowDatePickerChange = { showDatePicker = !showDatePicker },
                    onUpdateClick = {
                        showDatePicker = true
                    },
                    onDateSelected = {
                        onAction(ChildDetailAction.UpdateChild { copy(birthDate = it) })
                        showDatePicker = false
                    }
                )
                DropDownTextField(
                    expand = expandCrewState,
                    onExpandChange = { expandCrewState = !expandCrewState },
                    text = state.crews.find { it.id == state.child?.crewId }?.name ?: "",
                    label = stringResource(Res.string.crew),
                    items = state.crews,
                    onItemClick = { selectedCrew ->
                        onAction(ChildDetailAction.UpdateChild { copy(crewId = selectedCrew.id) })
                        onAction(ChildDetailAction.UpdateChild { copy(groupId = selectedCrew.groupId) })
                        onAction(ChildDetailAction.UpdateValidationState("crew"))
                        expandCrewState = false
                    },
                    keyboardController = keyboardController,
                    itemToString = { crew -> crew.name },
                    isValid = state.isCrewValid,
                    errorMessage = stringResource(Res.string.validation_error_crew),
                    startPadding = 15.dp,
                    endPadding = 15.dp
                )
                DropDownTextField(
                    expand = expandRoleState,
                    onExpandChange = { expandRoleState = !expandRoleState },
                    text = state.child?.role?.let { getChildRoleName(it) } ?: "",
                    label = stringResource(Res.string.role),
                    items = ChildRole.entries,
                    onItemClick = { selectedRole ->
                        onAction(ChildDetailAction.UpdateChild { copy(role = selectedRole) })
                        onAction(ChildDetailAction.UpdateValidationState("role"))
                        expandRoleState = false
                    },
                    keyboardController = keyboardController,
                    itemToString = { getChildRoleName(it) },
                    isValid = state.isRoleValid,
                    errorMessage = stringResource(Res.string.validation_error_role),
                    startPadding = 15.dp,
                    endPadding = 15.dp
                )
                DropDownTextField(
                    expand = expandCategoryState,
                    onExpandChange = { expandCategoryState = !expandCategoryState },
                    text = state.categories.find { it.id == state.child?.trailCategoryId }?.description
                        ?: "",
                    label = stringResource(Res.string.trail_categories),
                    items = state.categories,
                    onItemClick = { selectedCategory ->
                        onAction(ChildDetailAction.UpdateChild { copy(trailCategoryId = selectedCategory.id) })
                        onAction(ChildDetailAction.UpdateValidationState("category"))
                        expandCategoryState = false
                    },
                    keyboardController = keyboardController,
                    itemToString = { category -> category.description },
                    isValid = state.isCategoryValid,
                    errorMessage = stringResource(Res.string.validation_error_trail_category),
                    startPadding = 15.dp,
                    endPadding = 15.dp
                )
                Switcher(
                    selectedButtonIndex = state.selectedActiveButtonIndex,
                    firstLabel = stringResource(Res.string.active),
                    secondLabel = stringResource(Res.string.nonactive),
                    onFirstClick = { onAction(ChildDetailAction.UpdateChild { copy(isActive = true) }) },
                    onSecondClick = { onAction(ChildDetailAction.UpdateChild { copy(isActive = false) }) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
