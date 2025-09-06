package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.negative_points_all_records

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.TripleChoiceIconFilterRow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_worked_off
import sikula_kmp.composeapp.generated.resources.shovel
import sikula_kmp.composeapp.generated.resources.users_three

@Composable
fun WorkedOffFilterRow(
    selectedButtonIndex: Int,
    onFirstClick: () -> Unit,
    onSecondClick: () -> Unit,
    onThirdClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TripleChoiceIconFilterRow(
        selectedButtonIndex = selectedButtonIndex,
        firstIcon = {
            Icon(
                painter = painterResource(Res.drawable.users_three),
                modifier = Modifier.size(25.dp),
                contentDescription = "Všichni",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        secondIcon = {
            Icon(
                painter = painterResource(Res.drawable.shovel),
                modifier = Modifier.size(25.dp),
                contentDescription = stringResource(Res.string.description_worked_off),
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        thirdIcon = {
            Icon(
                painter = painterResource(Res.drawable.shovel),
                modifier = Modifier.size(25.dp),
                contentDescription = stringResource(Res.string.description_worked_off),
                tint = Color(0xFF9194A6)
            )
        },
        onFirstClick = onFirstClick,
        onSecondClick = onSecondClick,
        onThirdClick = onThirdClick,
        modifier = modifier
    )
}