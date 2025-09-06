package cz.bosan.sikula_kmp.features.discipline_management.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.domain.Platform
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline

@Composable
fun NumberDisciplineField(
    value: String?,
    onValueChange: (String?) -> Unit,
    hideKeyboardAfterCheck: Boolean,
    isNextRecord: Boolean,
    discipline: Discipline,
    modifier: Modifier = Modifier
) {
    var textState by remember { mutableStateOf(TextFieldValue(value ?: "")) }
    var isFocused by remember { mutableStateOf(false) }
    var wasDoneManually by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester: FocusRequester? = if (isNextRecord) remember { FocusRequester() } else null

    LaunchedEffect(value) {
        if (value != textState.text) {
            textState = TextFieldValue(value ?: "")
            focusRequester?.requestFocus()
        }
    }

    LaunchedEffect(isNextRecord) {
        if (isNextRecord) {
            focusRequester?.requestFocus()
        }
    }

    Box(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        BasicTextField(
            value = textState,
            onValueChange = { newValue ->
                val text = newValue.text

                val isValid = when {
                    text.isEmpty() -> true
                    text.all { it.isDigit() } -> true
                    discipline == Discipline.Team.CORRECTIONS &&
                            (text == "-" || (text.startsWith("-") && text.drop(1).all { it.isDigit() })) -> true
                    discipline == Discipline.Individual.MORSE &&
                            Regex("^\\d*(\\.\\d*)?$").matches(text) -> true
                    else -> false
                }

                if (isValid) {
                    textState = newValue
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (Platform.isIos) KeyboardType.Text else KeyboardType.Number,
                imeAction = ImeAction.Default
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onValueChange(textState.text.ifEmpty { null })
                    wasDoneManually = true
                    if (hideKeyboardAfterCheck) {
                        keyboardController?.hide()
                        isFocused = false
                    }
                }
            ),
            modifier = if (isNextRecord) {
                Modifier.focusRequester(focusRequester!!)
            } else {
                Modifier
            }
                .width(62.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    shape = MaterialTheme.shapes.small
                )
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 6.dp, vertical = 8.dp)
                .onFocusChanged {
                    if (isFocused && !it.hasFocus) {
                        if (!wasDoneManually && textState.text.isNotBlank()) {
                            onValueChange(textState.text.ifEmpty { null })
                        }
                    }
                    isFocused = it.hasFocus
                },
        )
    }
}