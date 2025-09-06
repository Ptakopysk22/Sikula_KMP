package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.crew_records

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrewRecordsTopBar(
    onBackClick: () -> Unit,
    crew: Crew,
    modifier: Modifier = Modifier,
) {

    CenterAlignedTopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    TextButton(
                        onClick = {
                            onBackClick()
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = stringResource(Res.string.back),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.offset(x = (-6).dp),
                        )
                    }
                }
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.weight(1f)
                        .padding(horizontal = 12.dp),
                ) {

                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(133.dp)
                        .height(43.dp)
                        .border(4.dp, color = Color.Black, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = crew.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
    )
}