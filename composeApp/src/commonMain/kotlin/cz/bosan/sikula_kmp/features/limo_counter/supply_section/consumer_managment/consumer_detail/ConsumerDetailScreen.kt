package cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.consumer_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.app.NavigationBarActions
import cz.bosan.sikula_kmp.core.domain.Destination
import cz.bosan.sikula_kmp.core.domain.NFCManager
import cz.bosan.sikula_kmp.core.domain.newItemID
import cz.bosan.sikula_kmp.core.presentation.components.FloatingActionButton
import cz.bosan.sikula_kmp.core.presentation.components.MainTopBar
import cz.bosan.sikula_kmp.core.presentation.components.NavigationBar
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.core.presentation.components.forms.DropDownTextField
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_add

@Composable
fun ConsumerDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: ConsumerDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    nfcManager: NFCManager,
    navigationBarActions: NavigationBarActions,
    onAddConsumptionRecordClick: (Int) -> Unit,
    onAddDeposit: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    ConsumerDetailScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onBackClick = {
            nfcManager.stopReading()
            onBackClick()
        },
        nfcManager = nfcManager,
        navigationBarActions = navigationBarActions,
        onAddConsumptionRecordClick = onAddConsumptionRecordClick,
        onAddDeposit = onAddDeposit

    )

}

@Composable
private fun ConsumerDetailScreen(
    state: ConsumerDetailState,
    modifier: Modifier = Modifier,
    onAction: (ConsumerDetailAction) -> Unit,
    onBackClick: () -> Unit,
    nfcManager: NFCManager,
    navigationBarActions: NavigationBarActions,
    onAddConsumptionRecordClick: (Int) -> Unit,
    onAddDeposit: (Int) -> Unit,
) {

    var expandLeadersState by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val creditTransactionListState = rememberLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MainTopBar(
                currentLeader = state.currentLeader,
                textInBox = state.consumer?.leader?.nickName ?: "Nový konzument",
                showProfile = false,
                onBackClick = onBackClick,
                keyboardController = keyboardController,
                showButton = false
            )
        }, bottomBar = {
            state.currentLeader.leader.role.let {
                NavigationBar(
                    role = it,
                    currentDestination = Destination.CONSUMER_MANAGER,
                    navigationBarActions = navigationBarActions,
                )
            }
        }, floatingActionButton = {
            state.consumerId?.let {
                FloatingActionButton(
                    onClick = { onAddConsumptionRecordClick(it) },
                    icon = rememberVectorPainter(image = Icons.Default.Add),
                    description = stringResource(Res.string.description_add)
                )
            }
        }
    ) { innerPadding ->
        WrapBox(
            isLoading = state.isLoading,
            errorMessage = state.errorMessage,
            content = {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    //.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (state.consumerId == newItemID) {
                        DropDownTextField(
                            expand = expandLeadersState,
                            onExpandChange = { expandLeadersState = !expandLeadersState },
                            text = "",
                            label = "Konzument",
                            items = state.potentialConsumers,
                            onItemClick = { selectedLeader ->
                                onAction(ConsumerDetailAction.AssignConsumer(selectedLeader))
                                expandLeadersState = false
                            },
                            keyboardController = keyboardController,
                            itemToString = { leader -> leader.nickName },
                            startPadding = 15.dp,
                            endPadding = 15.dp
                        )
                    }
                    TagBox( //přidat vynulování čipu
                        tag = state.consumer?.consumer?.tag,
                        isTagReading = state.readingTag,
                        onStartReading = {
                            onAction(ConsumerDetailAction.ChangeReadingTagState(true))
                            nfcManager.startReading { text ->
                                onAction(ConsumerDetailAction.UpdateTag(text))
                                onAction(
                                    ConsumerDetailAction.ChangeReadingTagState(
                                        false
                                    )
                                )
                            }
                        },
                        stopReadingTag = {
                            nfcManager.stopReading()
                            onAction(ConsumerDetailAction.ChangeReadingTagState(false))
                        }
                    )
                    Box(
                        modifier = Modifier.height(25.dp).fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) { Text("Kredit: ${state.consumer?.consumer?.credit} Kč") }
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Button(
                                onClick = { onAddDeposit(state.consumerId!!) },
                                shape = CircleShape,
                                enabled = (state.consumerId != newItemID),
                                elevation = ButtonDefaults.buttonElevation(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                content = {
                                    Icon(
                                        painter = rememberVectorPainter(Icons.Default.Add),
                                        modifier = Modifier.size(45.dp),
                                        contentDescription = "Správa kreditu"
                                    )
                                })
                        }
                    }
                    HorizontalDivider(
                        thickness = 4.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Seznam pohybů: ")
                    CreditTransactionList(
                        creditTransactions = state.creditTransactions,
                        onTransactionClick = {},
                        scrollState = creditTransactionListState
                    )
                }
            }
        )
    }
}

