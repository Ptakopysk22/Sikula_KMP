package cz.bosan.sikula_kmp.features.discipline_management.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.barriers
import sikula_kmp.composeapp.generated.resources.description_worked_off
import sikula_kmp.composeapp.generated.resources.shovel
import sikula_kmp.composeapp.generated.resources.wheelchair

@Composable
fun IconCheckBox(
    discipline: Discipline,
    isChecked: Boolean?,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { onCheckedChange(if (isChecked != null) !isChecked else false) },
        modifier = modifier
    ) {
        Icon(
            painter = if (discipline == Discipline.Individual.NEGATIVE_POINTS)
                painterResource(Res.drawable.shovel)
            else painterResource(Res.drawable.wheelchair),
            tint = if (isChecked == null)
                MaterialTheme.colorScheme.error
            else if (discipline == Discipline.Individual.TRAIL || discipline == Discipline.Team.BOAT_RACE) {
                if (!isChecked)
                    MaterialTheme.colorScheme.secondary
                else Color(0xFF9194A6)
            } else {
                if (!isChecked)
                    Color(0xFF9194A6)
                else MaterialTheme.colorScheme.secondary
            },
            contentDescription = if (discipline == Discipline.Individual.NEGATIVE_POINTS)
                stringResource(Res.string.description_worked_off)
            else
                (stringResource(Res.string.barriers))
        )
    }
}