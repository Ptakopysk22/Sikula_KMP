package cz.bosan.sikula_kmp.core.presentation.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.bosan.sikula_kmp.core.presentation.components.OutlinedBox
import cz.bosan.sikula_kmp.core.presentation.components.formatDate
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerComponent.WheelDatePicker
import network.chaintech.kmp_date_time_picker.utils.now
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.birthdate
import sikula_kmp.composeapp.generated.resources.description_update_birthdate
import sikula_kmp.composeapp.generated.resources.save
import sikula_kmp.composeapp.generated.resources.select_birthdate

@Composable
fun BirthDatePicker(
    birthDate: LocalDate,
    showDatePicker: Boolean,
    onShowDatePickerChange: () -> Unit,
    onUpdateClick: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {

    OutlinedBox(
        title = stringResource(Res.string.birthdate),
        content = {
            Box(
                modifier = modifier.height(25.dp).fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = formatDate(birthDate),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    IconButton(
                        onClick = { onUpdateClick() },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(Res.string.description_update_birthdate),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)
                        )
                    }
                }
            }
        })

    if (showDatePicker) {
        Dialog(onDismissRequest = { onShowDatePickerChange() }) {
            Surface(
                modifier = Modifier
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 4.dp,
            ) {
                var selectedDate by remember { mutableStateOf(birthDate) }
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.select_birthdate),
                        style = MaterialTheme.typography.titleMedium
                    )

                    WheelDatePicker(
                        startDate = birthDate,
                        hideHeader = true,
                        yearsRange = 1930..LocalDate.now().year,
                        rowCount = 3,
                        onDateChangeListener = { selectedDate = it },
                    )

                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = {
                            onDateSelected(selectedDate)
                        }
                    ) {
                        Text(stringResource(Res.string.save))
                    }
                }
            }
        }
    }
}