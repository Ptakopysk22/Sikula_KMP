package cz.bosan.sikula_kmp.core.presentation.components.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.getScreenWidth
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_expand_menu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropDownTextField(
    expand: Boolean,
    onExpandChange: (Boolean) -> Unit,
    text: String,
    label: String,
    items: List<T>,
    onItemClick: (T) -> Unit,
    keyboardController: SoftwareKeyboardController?,
    itemToString: @Composable (T) -> String,
    isValid: Boolean = true,
    errorMessage: String = "",
    startPadding: Dp,
    endPadding: Dp,
    dropDownStartPadding: Dp = startPadding,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = startPadding, top = 5.dp, end = endPadding, bottom = 5.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expand,
            onExpandedChange = { onExpandChange(it) },
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 8.dp, bottom = if (isValid) 0.dp else 16.dp)
                    .background(
                        if (text.isEmpty()) {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        shape = MaterialTheme.shapes.medium
                    )
            )
            Column {
                OutlinedTextField(
                    value = text,
                    label = { Text(label, style = MaterialTheme.typography.titleSmall) },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = if (expand) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                            contentDescription = stringResource(Res.string.description_expand_menu),
                        )
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                        .clickable {
                            keyboardController?.hide()
                        }
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
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
            ExposedDropdownMenu(
                expanded = expand,
                onDismissRequest = { onExpandChange(false) },
                modifier = Modifier
                    .width(getScreenWidth() - (dropDownStartPadding + endPadding))
                    .heightIn(max = 235.dp)
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium,
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                itemToString(item),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            onItemClick(item)
                        }
                    )
                }
            }
        }
    }
}
