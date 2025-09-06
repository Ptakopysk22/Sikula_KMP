package cz.bosan.sikula_kmp.features.points_management.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.BackButtonRow
import cz.bosan.sikula_kmp.core.presentation.components.ProfilePicture
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.presentation.getDisciplineName
import cz.bosan.sikula_kmp.managers.leader_manager.domain.CurrentLeader
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.unknown_crew

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsTopBar(
    onBackClick: () -> Unit,
    discipline: Discipline?,
    crew: Crew?,
    isFirstScreen: Boolean = false,
    currentLeader: CurrentLeader = CurrentLeader.EMPTY,
    onProfileClick: () -> Unit = {},
    keyboardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier,
) {
    val color: Color = discipline?.getColor() ?: (crew?.color ?: Color.Black)
    val title: String = if (discipline != null && crew != null) {
        "${crew.name}: ${getDisciplineName(discipline)}"
    } else if (discipline != null) {
        getDisciplineName(discipline)
    } else crew?.name ?: stringResource(Res.string.unknown_crew)

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
            if ((currentLeader.leader.role == Role.DIRECTOR) && isFirstScreen) {
                Row(
                    modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onProfileClick,
                        modifier = Modifier.size(37.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        content = {
                            ProfilePicture(
                                currentLeader = currentLeader,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    )
                }
            } else {
                BackButtonRow(
                    onBackClick = onBackClick,
                    keyboardController = keyboardController,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .wrapContentWidth()
                    .height(37.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = MaterialTheme.shapes.medium,
                        spotColor = color,
                        ambientColor = color,
                        clip = false
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.medium
                    )
                    .border(
                        width = 2.dp,
                        color = color,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}