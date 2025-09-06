package cz.bosan.sikula_kmp.features.limo_counter.supply_section.product_management.product_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.bosan.sikula_kmp.core.domain.newItemID
import cz.bosan.sikula_kmp.core.presentation.components.MainTopBar
import cz.bosan.sikula_kmp.core.presentation.components.WrapBox
import cz.bosan.sikula_kmp.core.presentation.components.formatDateTime
import cz.bosan.sikula_kmp.core.presentation.components.forms.NumberField
import cz.bosan.sikula_kmp.core.presentation.components.forms.TextField
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.save
import sikula_kmp.composeapp.generated.resources.title
import sikula_kmp.composeapp.generated.resources.validation_error_title

@Composable
fun ProductDetailRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProductDetailScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@Composable
private fun ProductDetailScreen(
    state: ProductDetailState,
    modifier: Modifier,
    onAction: (ProductDetailAction) -> Unit,
    onBackClick: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var boughtAmountState by remember {
        mutableStateOf(
            TextFieldValue(
                state.boughtAmountString ?: ""
            )
        )
    }
    var purchasePriceState by remember {
        mutableStateOf(
            TextFieldValue(
                state.purchasedPriceString ?: ""
            )
        )
    }
    var salePriceState by remember { mutableStateOf(TextFieldValue(state.salePriceString ?: "")) }

    LaunchedEffect(state.boughtAmountString) {
        if (boughtAmountState.text != (state.boughtAmountString ?: "")) {
            boughtAmountState = TextFieldValue(state.boughtAmountString ?: "")
        }
    }

    LaunchedEffect(state.purchasedPriceString) {
        if (purchasePriceState.text != (state.purchasedPriceString ?: "")) {
            purchasePriceState = TextFieldValue(state.purchasedPriceString ?: "")
        }
    }

    LaunchedEffect(state.salePriceString) {
        if (salePriceState.text != (state.salePriceString ?: "")) {
            salePriceState = TextFieldValue(state.salePriceString ?: "")
        }
    }

    LaunchedEffect(state.productCreateSuccessfully) {
        if (state.productCreateSuccessfully) {
            keyboardController?.hide()
            onBackClick()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =
        {
            MainTopBar(
                currentLeader = state.currentLeader,
                textInBox = if (state.product.id == newItemID) "Nová položka" else "Položka ID ${state.product.id}",
                showProfile = false,
                onBackClick = {
                    keyboardController?.hide()
                    onBackClick()
                },
                showButton = true,
                onButtonClick = { onAction(ProductDetailAction.OnSaveProductClick) },
                buttonContent = {
                    Text(
                        text = stringResource(Res.string.save),
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                enabledButton = true
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
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        TextField(
                            text = state.product.name,
                            onTextChange = {
                                onAction(ProductDetailAction.UpdateProduct { copy(name = it) })
                                onAction(ProductDetailAction.UpdateValidationState("name"))
                            },
                            label = stringResource(Res.string.title),
                            keyboardController = keyboardController,
                            isValid = state.isNameValid,
                            errorMessage = stringResource(Res.string.validation_error_title)
                        )

                        NumberField(
                            textState = boughtAmountState,
                            negativeNumbersAllowed = false,
                            doubleAllowed = false,
                            onTextChange = {
                                boughtAmountState = it
                                onAction(
                                    ProductDetailAction.UpdateNumber(
                                        number = it.text,
                                        numberTyp = "boughtAmount"
                                    )
                                )
                                onAction(ProductDetailAction.UpdateValidationState("boughtAmount"))
                            },
                            label = "Nakoupené množství",
                            enabled = true,
                            keyboardController = keyboardController,
                            isValid = state.isBoughtAmountValid,
                            errorMessage = "Nakoupené množství musí být přitozené číslo",
                        )

                        NumberField(
                            textState = purchasePriceState,
                            negativeNumbersAllowed = false,
                            doubleAllowed = true,
                            onTextChange = {
                                purchasePriceState = it
                                onAction(
                                    ProductDetailAction.UpdateNumber(
                                        number = it.text,
                                        numberTyp = "purchasePrice"
                                    )
                                )
                                onAction(ProductDetailAction.UpdateValidationState("purchasePrice"))
                            },
                            label = "Nákupní jednotková cena",
                            enabled = true,
                            keyboardController = keyboardController,
                            isValid = state.isPurchasePriceValid,
                            errorMessage = "Nákupní jednotková cena musí být nezáporné číslo",
                        )
                        NumberField(
                            textState = salePriceState,
                            negativeNumbersAllowed = false,
                            doubleAllowed = true,
                            onTextChange = {
                                salePriceState = it
                                onAction(
                                    ProductDetailAction.UpdateNumber(
                                        number = it.text,
                                        numberTyp = "salePrice"
                                    )
                                )
                                onAction(ProductDetailAction.UpdateValidationState("salePrice"))
                            },
                            label = "Prodejní jednotková cena",
                            enabled = true,
                            keyboardController = keyboardController,
                            isValid = state.isSalePriceValid,
                            errorMessage = "Prodejní jednotková cena musí být nezáporné číslo",
                        )
                        TextField(
                            text = formatDateTime(state.product.timeStamp),
                            onTextChange = {},
                            label = "Nakoupeno",
                            keyboardController = keyboardController,
                            enabled = false
                        )
                        TextField(
                            text = state.buyer.nickName,
                            onTextChange = {},
                            label = "Nakoupil",
                            keyboardController = keyboardController,
                            enabled = false
                        )
                        TextField(
                            text = state.product.comment,
                            onTextChange = {
                                onAction(ProductDetailAction.UpdateProduct { copy(comment = it) })
                                onAction(ProductDetailAction.UpdateValidationState("comment"))
                            },
                            label = "Poznámka",
                            keyboardController = keyboardController,
                            isValid = state.isCommentValid,
                            errorMessage = "Po úpravě musí být poznámka vyplněna"
                        )
                    }
                }
            )
        }
    }
}
