package cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.edit_deposit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.core.presentation.components.MainTopBar
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.core.presentation.components.forms.NumberField
import cz.bosan.sikula_kmp.core.presentation.components.forms.Switcher
import cz.bosan.sikula_kmp.core.presentation.components.forms.TextField
import cz.bosan.sikula_kmp.features.limo_counter.supply_section.components.QrCodeImage
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.paper_plane_right

@Composable
fun EditDepositRoute(
    modifier: Modifier = Modifier,
    viewModel: EditDepositViewModel = koinViewModel(),
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EditDepositScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@Composable
private fun EditDepositScreen(
    state: EditDepositState,
    modifier: Modifier = Modifier,
    onAction: (EditDepositAction) -> Unit,
    onBackClick: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var moneyAmountState by remember { mutableStateOf(TextFieldValue(state.moneyAmount ?: "")) }

    LaunchedEffect(state.moneyAmount) {
        if (moneyAmountState.text != (state.moneyAmount ?: "")) {
            moneyAmountState = TextFieldValue(state.moneyAmount ?: "")
        }
    }

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) {
            keyboardController?.hide()
            onBackClick()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MainTopBar(
                currentLeader = state.currentLeader,
                textInBox = "Správa záloh",
                showProfile = false,
                onBackClick = {
                    keyboardController?.hide()
                    onBackClick()
                },
                keyboardController = keyboardController,
                showButton = true,
                onButtonClick = {
                    keyboardController?.hide()
                    onAction(EditDepositAction.CreateDepositRecord)
                },
                buttonContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Zaúčtovat",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Icon(
                            painter = painterResource(Res.drawable.paper_plane_right),
                            contentDescription = "Zaúčtovat",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                enabledButton = state.enabledSave
            )
        }
    ) { innerPadding ->
        WrapBox(
            isLoading = state.isLoading,
            errorMessage = state.errorMessage,
            content = {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Text(text = "Konzument: ${state.consumer.leader.nickName}")
                    Text(text = "Kredit: ${state.consumer.consumer.credit},-Kč")

                    if (state.accounts.isNotEmpty()) {
                        Switcher(
                            selectedButtonIndex = state.selectedAccountIndex,
                            firstLabel = state.accounts[0].name,
                            secondLabel = state.accounts[1].name,
                            onFirstClick = { onAction(EditDepositAction.OnAccountChange(0)) },
                            onSecondClick = { onAction(EditDepositAction.OnAccountChange(1)) },
                        )
                    }

                    NumberField(
                        textState = moneyAmountState,
                        negativeNumbersAllowed = true,
                        doubleAllowed = true,
                        onTextChange = {
                            moneyAmountState = it
                            onAction(EditDepositAction.ChangeMoneyAmount(it.text))
                        },
                        label = "Kredit",
                        enabled = true,
                        keyboardController = keyboardController,
                        isValid = true,
                        errorMessage = "",
                    )
                    TextField(
                        text = state.comment,
                        onTextChange = {
                            onAction(EditDepositAction.ChangeComment(it))
                        },
                        label = "Interní poznámka",
                        keyboardController = keyboardController,
                    )
                    state.qrCodStateMessage?.asString()?.let {
                        Message(
                            text = it,
                            messageTyp = MessageTyp.INFO,
                        )
                    }
                    state.qrCode?.let {
                        QrCodeImage(byteArray = it, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        )
    }
}

