package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.negative_points_recording

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.BackButtonRow
import cz.bosan.sikula_kmp.core.presentation.components.PrimaryButton
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.save
import sikula_kmp.composeapp.generated.resources.search_child

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegativePointsRecordingTopBar(
    onBackClick: () -> Unit,
    keyboardController: SoftwareKeyboardController?,
    showSaveButton: Boolean,
    enabledSaveButton: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {},
        modifier = modifier,
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = MaterialTheme.colorScheme.background
        ),
        navigationIcon = {},
        actions = {}
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = if(showSaveButton) 0.dp else 8.dp, bottom = 2.dp)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BackButtonRow(
                onBackClick = onBackClick,
                keyboardController = keyboardController,
                modifier = modifier.align(Alignment.CenterStart)
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (!showSaveButton) {
                    Text(
                        text = stringResource(Res.string.search_child),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showSaveButton) {
                    PrimaryButton(
                        content = {
                            Text(
                                text = stringResource(Res.string.save),
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            onSaveClick()
                            keyboardController?.hide()
                        },
                        enabled = enabledSaveButton,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
    }
}


