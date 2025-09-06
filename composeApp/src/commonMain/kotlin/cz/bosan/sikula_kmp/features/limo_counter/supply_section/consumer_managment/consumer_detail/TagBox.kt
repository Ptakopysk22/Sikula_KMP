package cz.bosan.sikula_kmp.features.limo_counter.supply_section.consumer_managment.consumer_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.components.OutlinedBox
import org.jetbrains.compose.resources.painterResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.stop

@Composable
fun TagBox(
    tag: String?,
    isTagReading: Boolean,
    onStartReading: () -> Unit,
    stopReadingTag: () -> Unit,
    modifier: Modifier = Modifier
) {

    OutlinedBox(
        title = "NFC tag",
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
                        text = if (isTagReading) "Přilož čip" else tag ?: "Bez čipu",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    IconButton(
                        onClick = {
                            if (isTagReading) {
                                stopReadingTag()
                            } else {
                                onStartReading()
                            }
                        },
                    ) {
                        if (isTagReading) {
                            Icon(
                                painter = painterResource(Res.drawable.stop),
                                contentDescription = "Uprav čip",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Uprav čip",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)
                            )
                        }
                    }
                }
            }
        })

}