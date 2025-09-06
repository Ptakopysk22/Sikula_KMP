package cz.bosan.sikula_kmp.features.attendee_management.check_user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_add_child
import sikula_kmp.composeapp.generated.resources.description_add_leader
import sikula_kmp.composeapp.generated.resources.description_more_information
import sikula_kmp.composeapp.generated.resources.info

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckUserTopBar(
    onBackClick: () -> Unit,
    showInfoChange: () -> Unit = {},
    keyboardController: SoftwareKeyboardController?,
    isCheckingLeader: Boolean,
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
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 2.dp)
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
                Text(
                    text = if (isCheckingLeader) stringResource(Res.string.description_add_leader) else stringResource(
                        Res.string.description_add_child
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = showInfoChange,
                    modifier = Modifier.padding(horizontal = 6.dp),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.info),
                        contentDescription = stringResource(Res.string.description_more_information),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}