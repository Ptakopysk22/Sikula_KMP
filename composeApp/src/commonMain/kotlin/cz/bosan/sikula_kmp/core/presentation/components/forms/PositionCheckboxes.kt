package cz.bosan.sikula_kmp.core.presentation.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.OutlinedBox
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.managers.leader_manager.presentation.getPositionName
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.positions

@Composable
fun PositionCheckboxes(
    leaderPositions: List<Position>,
    onPositionChange: (Position, Boolean) -> Unit,
    role: Role,
    modifier: Modifier = Modifier
) {
    val allPositions = if (role == Role.DIRECTOR || role == Role.GAME_MASTER) {
        listOf(Position.BADGES_MASTER)
    } else {
        Position.entries - Position.UNKNOWN_POSITION
    }

    OutlinedBox(
        title = stringResource(Res.string.positions),
        modifier = modifier,
        content = {
            Column {
                allPositions.chunked(2).forEach { chunk ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(3.dp)
                    ) {
                        chunk.forEach { position ->
                            val isChecked = position in leaderPositions

                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        onPositionChange(
                                            position,
                                            checked
                                        )
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.secondary)
                                )
                                Text(
                                    text = getPositionName(position),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}