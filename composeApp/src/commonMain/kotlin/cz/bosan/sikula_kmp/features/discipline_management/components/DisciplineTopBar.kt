package cz.bosan.sikula_kmp.features.discipline_management.components

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.BackButtonRow
import cz.bosan.sikula_kmp.core.presentation.components.SecondaryButton
import cz.bosan.sikula_kmp.core.presentation.components.TopBox
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.DayRecordsState
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.presentation.getDisciplineName
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.approve_data
import sikula_kmp.composeapp.generated.resources.description_more_information
import sikula_kmp.composeapp.generated.resources.info
import sikula_kmp.composeapp.generated.resources.paper_plane_right
import sikula_kmp.composeapp.generated.resources.send_data

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisciplineTopBar(
    onBackClick: () -> Unit,
    discipline: Discipline,
    dayRecordsState: DayRecordsState,
    role: Role,
    isDisciplineMaster: Boolean? = null,
    showState: Boolean = false,
    showInfoIcon: Boolean,
    showInfoChange: () -> Unit = {},
    submitRecords: () -> Unit,
    unSubmitRecords: () -> Unit,
    keyboardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier,
) {
    val dayRecordStateRemembered by rememberUpdatedState(newValue = dayRecordsState)
    val isIconGray: Boolean = (role == Role.GAME_MASTER || role == Role.DIRECTOR)

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
                modifier = Modifier.align(Alignment.CenterStart),
            )
            TopBox(
                text = getDisciplineName(discipline),
                color = discipline.getColor(),
                modifier = Modifier.align(Alignment.Center)
            )
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showState) {
                    if ((role == Role.CHILD_LEADER || role == Role.HEAD_GROUP_LEADER) && isDisciplineMaster == null || isDisciplineMaster == true) {
                        when (dayRecordsState) {
                            DayRecordsState.NON_SYNCHRONIZE,
                            DayRecordsState.NON_CHECKED_BY_GROUP,
                            DayRecordsState.IN_PROGRESS -> {
                                SecondaryButton(
                                    content = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = stringResource(Res.string.send_data),
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSecondary
                                            )
                                            Icon(
                                                painter = painterResource(Res.drawable.paper_plane_right),
                                                contentDescription = stringResource(Res.string.send_data),
                                                tint = MaterialTheme.colorScheme.onSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    },
                                    enabled = dayRecordsState != DayRecordsState.IN_PROGRESS,
                                    onClick = { submitRecords() },
                                )
                            }

                            else -> DayStateCircleIcon(
                                dayRecordsState = dayRecordStateRemembered,
                                isGray = isIconGray,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    } else if (role == Role.GAME_MASTER) {
                        if (dayRecordsState == DayRecordsState.CHECKED_BY_GROUP || dayRecordsState == DayRecordsState.IN_PROGRESS) {
                            SecondaryButton(
                                content = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = stringResource(Res.string.approve_data),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                        Icon(
                                            painter = painterResource(Res.drawable.paper_plane_right),
                                            contentDescription = stringResource(Res.string.approve_data),
                                            tint = MaterialTheme.colorScheme.onSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                enabled = dayRecordsState != DayRecordsState.IN_PROGRESS,
                                onClick = { submitRecords() },
                            )
                        } else {
                            DayStateCircleIcon(
                                dayRecordsState = dayRecordStateRemembered,
                                isGray = isIconGray,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    } else {
                        DayStateCircleIcon(
                            dayRecordsState = dayRecordStateRemembered,
                            isGray = isIconGray,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
                if (showInfoIcon) {
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
}