package cz.bosan.sikula_kmp.features.discipline_management.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import cz.bosan.sikula_kmp.core.presentation.components.PrimaryButton
import cz.bosan.sikula_kmp.core.presentation.components.formatTrailTime
import kotlinx.datetime.LocalTime
import network.chaintech.kmp_date_time_picker.ui.timepicker.WheelTimePickerComponent.WheelTimePicker
import network.chaintech.kmp_date_time_picker.utils.TimeFormat
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.empty_duration
import sikula_kmp.composeapp.generated.resources.reset
import sikula_kmp.composeapp.generated.resources.save
import sikula_kmp.composeapp.generated.resources.select_duration

@Composable
fun TimePicker(
    time: LocalTime?,
    showTimePicker: Boolean,
    onShowTimePickerChange: () -> Unit,
    onUpdateClick: () -> Unit,
    onTimeSelected: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(37.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxHeight().clickable { onUpdateClick() },
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 5.dp
        ) {


            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                Text(
                    text = if (time == null) stringResource(Res.string.empty_duration) else formatTrailTime(time.minute + (time.hour * 60)),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }

    if (showTimePicker) {
        Dialog(onDismissRequest = { onShowTimePickerChange() }) {
            Surface(
                modifier = Modifier
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 4.dp,
            ) {
                var selectedTime by remember { mutableStateOf(time) }
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.select_duration),
                        style = MaterialTheme.typography.titleMedium
                    )

                    WheelTimePicker(
                        startTime = time ?: LocalTime(hour = 0, minute = 0, second = 0),
                        hideHeader = true,
                        rowCount = 3,
                        onTimeChangeListener = { selectedTime = it },
                        timeFormat = TimeFormat.HOUR_24,
                        height = 200.dp,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PrimaryButton(
                            enabled = true,
                            onClick = {
                                onTimeSelected(null)
                            },
                            content = {
                                Text(text = stringResource(Res.string.reset))
                            },
                        )
                        PrimaryButton(
                            enabled = true,
                            onClick = {
                                onTimeSelected(selectedTime)
                            },
                            content = {
                                Text(stringResource(Res.string.save))
                            },
                        )
                    }
                }
            }
        }
    }
}