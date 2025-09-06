package cz.bosan.sikula_kmp.core.presentation.components.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.SoftwareKeyboardController

@Composable
fun TextField(
    text: String,
    onTextChange: (String) -> Unit,
    label: String,
    enabled: Boolean = true,
    keyboardController: SoftwareKeyboardController?,
    isValid: Boolean = true,
    errorMessage: String = "",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp, bottom = if (isValid) 0.dp else 16.dp)
                .background(
                    if (!enabled) {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                    } else {
                        if (text.isEmpty()) {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                )
        )
        Column {
            OutlinedTextField(
                value = text,
                onValueChange = { onTextChange(it) },
                label = {
                    Text(
                        label,
                        style = if(text.isEmpty()){
                            MaterialTheme.typography.bodyMedium
                        } else{
                            MaterialTheme.typography.titleSmall
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                placeholder = { Text(label, style = MaterialTheme.typography.bodyMedium) },
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                enabled = enabled,
                isError = !isValid,
            )

            if (!isValid) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}