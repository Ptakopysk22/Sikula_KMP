package cz.bosan.sikula_kmp.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.forms.TextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.confirmation_of_absence
import sikula_kmp.composeapp.generated.resources.confirmation_of_absence_sentence
import sikula_kmp.composeapp.generated.resources.no
import sikula_kmp.composeapp.generated.resources.reason
import sikula_kmp.composeapp.generated.resources.service
import sikula_kmp.composeapp.generated.resources.validation_error_reason
import sikula_kmp.composeapp.generated.resources.yes

@Composable
fun <T> SwipeToDismissContainer(
    item: T,
    itemName: String,
    animationTime: Int = 300,
    confirmationDialog: Boolean = true,
    onDismiss: (T, String, onError: () -> Unit) -> Unit,
    showServiceIcon: Boolean = false,
    content: @Composable (T) -> Unit
) {
    var potentialDelete by remember { mutableStateOf(false) }
    var deleteItem by remember { mutableStateOf(false) }
    var comment by remember { mutableStateOf<String>("") }
    var stateToMaintain by remember { mutableStateOf<SwipeToDismissBoxValue?>(null) }

    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart,
                SwipeToDismissBoxValue.StartToEnd
                    -> {
                    if (confirmationDialog) {
                        potentialDelete = true
                    } else {
                        potentialDelete = false
                        deleteItem = true
                    }
                    stateToMaintain = dismissValue
                }

                else -> {}
            }
            false
        }
    )

    LaunchedEffect(stateToMaintain) {
        stateToMaintain?.let {
            state.snapTo(it)
            stateToMaintain = null
        }
    }

    LaunchedEffect(deleteItem) {
        if (deleteItem) {
            delay(animationTime.toLong())
            onDismiss(item, comment) {
                deleteItem = false
            }
        } else {
            state.reset()
        }
    }

    AnimatedVisibility(
        visible = !deleteItem,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationTime),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = state,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                )
            },
            content = {
                content(item)
            }
        )
    }

    val scope = rememberCoroutineScope()

    if (confirmationDialog && potentialDelete) {
        SwipeDismissConfirmationDialog(
            itemName = itemName,
            onCancel = {
                potentialDelete = false
                scope.launch { state.reset() }
            },
            onConfirm = { commentText ->
                potentialDelete = false
                comment = commentText
                deleteItem = true
            },
            showServiceIcon = showServiceIcon
        )
    }
}

@Composable
fun SwipeDismissConfirmationDialog(
    itemName: String,
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit,
    showServiceIcon: Boolean = false,
    comment: String = ""
) {
    var text by remember { mutableStateOf(comment) }
    var isValid by remember { mutableStateOf(true) }

    AlertDialog(
        title = { Text(text = stringResource(Res.string.confirmation_of_absence)) },
        text = {
            Column {
                Text(stringResource(Res.string.confirmation_of_absence_sentence, itemName))
                TextField(
                    text = text,
                    onTextChange = {
                        text = it
                        isValid = true
                    },
                    label = stringResource(Res.string.reason),
                    keyboardController = LocalSoftwareKeyboardController.current,
                    isValid = isValid,
                    errorMessage = stringResource(Res.string.validation_error_reason),
                )
                if (showServiceIcon) {
                    val text = stringResource(Res.string.service)
                    PrimaryButton(
                        enabled = true,
                        onClick = {
                            onConfirm(text)
                        },
                        modifier = Modifier.padding(horizontal = 10.dp),
                        content = {
                            Text(text = text, style = MaterialTheme.typography.labelLarge)
                        },
                    )
                }
            }
        },
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = {
                if (text.isEmpty()) {
                    isValid = false
                } else {
                    onConfirm(text)
                }
            }) {
                Text(text = stringResource(Res.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(Res.string.no))
            }
        }
    )
}
