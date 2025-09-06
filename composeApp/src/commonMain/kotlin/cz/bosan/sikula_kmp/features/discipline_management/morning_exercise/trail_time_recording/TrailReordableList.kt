package cz.bosan.sikula_kmp.features.discipline_management.morning_exercise.trail_time_recording

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.ReorderHapticFeedbackType
import cz.bosan.sikula_kmp.core.presentation.rememberReorderHapticFeedback
import cz.bosan.sikula_kmp.core.presentation.components.SwipeToDismissContainer
import cz.bosan.sikula_kmp.core.presentation.components.formatTrailTime
import cz.bosan.sikula_kmp.features.discipline_management.components.IconCheckBox
import cz.bosan.sikula_kmp.managers.children_manager.domain.Child
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.TargetImprovement
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_reorder
import sikula_kmp.composeapp.generated.resources.empty_duration
import sikula_kmp.composeapp.generated.resources.empty_value
import sikula_kmp.composeapp.generated.resources.goal_record_format
import sikula_kmp.composeapp.generated.resources.hand
import sikula_kmp.composeapp.generated.resources.hand_grabbing
import sikula_kmp.composeapp.generated.resources.last_record_format

@Composable
fun TrailReorderableList(
    children: List<Child>,
    targetImprovements: List<TargetImprovement>,
    countsForImprovementMap: Map<Int, Boolean>,
    onChangeCountsForImprovementMap: (Int, Boolean) -> Unit,
    onChildDismiss: (Child, Int, String) -> Unit,
    onChildMove: (Int, Int) -> Unit,
) {
    val haptic = rememberReorderHapticFeedback()

    var list by remember { mutableStateOf(children) }
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        list = list.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        onChildMove(from.index, to.index)
        haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(vertical = 4.dp),
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(list, key = { _, child -> child.id }) { index, child ->
            SwipeToDismissContainer(
                item = child,
                itemName = child.nickName,
                onDismiss = { childToDismiss, comment, onError ->
                    onChildDismiss(childToDismiss, index, comment)
                    list = list.toMutableList().apply { removeAt(index) }
                }
            ) { childItem ->
                ReorderableItem(reorderableLazyColumnState, child.id) { isDragging ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val targetImprovement = targetImprovements.find { it.id == child.id }

                    ElevatedCard(
                        onClick = {},
                        modifier = Modifier
                            .height(47.dp)
                            .padding(horizontal = 20.dp)
                            .semantics {
                                customActions = listOf(
                                    CustomAccessibilityAction(
                                        label = "Move Up",
                                        action = {
                                            if (index > 0) {
                                                list = list.toMutableList().apply {
                                                    add(index - 1, removeAt(index))
                                                }
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                    ),
                                    CustomAccessibilityAction(
                                        label = "Move Down",
                                        action = {
                                            if (index < list.size - 1) {
                                                list = list.toMutableList().apply {
                                                    add(index + 1, removeAt(index))
                                                }
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                    ),
                                )
                            },
                        shape = MaterialTheme.shapes.small,
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        interactionSource = interactionSource,
                    ) {
                        Row(
                            Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                (index + 1).toString(),
                                Modifier.padding(horizontal = 10.dp),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(child.nickName, Modifier.padding(horizontal = 4.dp))
                            Spacer(Modifier.weight(1f))
                            IconCheckBox(
                                isChecked = countsForImprovementMap[child.id],
                                onCheckedChange = { newValue ->
                                    onChangeCountsForImprovementMap(child.id, newValue)
                                },
                                discipline = Discipline.Individual.TRAIL
                            )
                            Column(
                                modifier = Modifier.padding(horizontal = 4.dp).width(120.dp)
                            ) {
                                    Text(
                                        text = if ( targetImprovement?.improvementValue == null ||  targetImprovement.improvementString == null) {
                                            stringResource(
                                                Res.string.last_record_format,
                                                stringResource(Res.string.empty_duration),
                                                stringResource(Res.string.empty_value)
                                            )
                                        } else {
                                            stringResource(
                                                Res.string.last_record_format,
                                                formatTrailTime(targetImprovement.improvementValue),
                                                targetImprovement.improvementString.toString()
                                            )
                                        },
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = if (targetImprovement?.improvementTargetValue == null || targetImprovement.improvementTargetString == null) {
                                            stringResource(
                                                Res.string.goal_record_format,
                                                stringResource(Res.string.empty_duration),
                                                stringResource(Res.string.empty_value)
                                            )
                                        } else {
                                            stringResource(
                                                Res.string.goal_record_format,
                                                formatTrailTime(targetImprovement.improvementTargetValue),
                                                targetImprovement.improvementTargetString.toString()
                                            )
                                        },
                                        style = MaterialTheme.typography.titleSmall
                                    )
                            }
                            IconButton(
                                modifier = Modifier
                                    .draggableHandle(
                                        onDragStarted = {
                                            haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
                                        },
                                        onDragStopped = {
                                            haptic.performHapticFeedback(ReorderHapticFeedbackType.END)
                                        },
                                        interactionSource = interactionSource,
                                    )
                                    .clearAndSetSemantics { },
                                onClick = {},
                            ) {
                                Icon(
                                    painter =
                                    if (isDragging) painterResource(Res.drawable.hand_grabbing)
                                    else painterResource(Res.drawable.hand),
                                    contentDescription = stringResource(Res.string.description_reorder)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}