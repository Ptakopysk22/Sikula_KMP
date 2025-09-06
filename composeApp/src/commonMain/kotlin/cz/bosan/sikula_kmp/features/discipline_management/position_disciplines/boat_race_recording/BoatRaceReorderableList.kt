package cz.bosan.sikula_kmp.features.discipline_management.position_disciplines.boat_race_recording

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.ReorderHapticFeedbackType
import cz.bosan.sikula_kmp.core.presentation.components.SwipeToDismissContainer
import cz.bosan.sikula_kmp.core.presentation.components.formatTrailTime
import cz.bosan.sikula_kmp.core.presentation.rememberReorderHapticFeedback
import cz.bosan.sikula_kmp.features.discipline_management.components.IconCheckBox
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.TargetImprovement
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.description_lock_un_lock
import sikula_kmp.composeapp.generated.resources.description_reorder
import sikula_kmp.composeapp.generated.resources.empty_duration
import sikula_kmp.composeapp.generated.resources.empty_value
import sikula_kmp.composeapp.generated.resources.goal_record_format
import sikula_kmp.composeapp.generated.resources.hand
import sikula_kmp.composeapp.generated.resources.hand_grabbing
import sikula_kmp.composeapp.generated.resources.last_record_format
import sikula_kmp.composeapp.generated.resources.lock_key
import sikula_kmp.composeapp.generated.resources.lock_key_open

@Composable
fun BoatRaceReorderableList(
    crews: List<Crew>,
    crewsLocks: Map<Int, Boolean>,
    targetImprovements: List<TargetImprovement>,
    countsForImprovementMap: Map<Int, Boolean>,
    onChangeCountsForImprovementMap: (Int, Boolean) -> Unit,
    onCrewDismiss: (Crew, Int, String) -> Unit,
    onCrewMove: (List<Crew>) -> Unit,
    onLockChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberReorderHapticFeedback()

    var list by remember { mutableStateOf(crews) }
    val lazyListState = rememberLazyListState()
    var moveFromIndex by remember { mutableStateOf<Int?>(null) }
    var moveToIndex by remember { mutableStateOf<Int?>(null) }

    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromCrew = list[from.index]
        val toCrew = list[to.index]
        if (crewsLocks[toCrew.id] == true) return@rememberReorderableLazyListState

        val unlockedList = list.filter { crewsLocks[it.id] != true }.toMutableList()
        val fromUnlockedIndex = unlockedList.indexOf(fromCrew)
        val toUnlockedIndex = unlockedList.indexOf(toCrew)

        unlockedList.add(toUnlockedIndex, unlockedList.removeAt(fromUnlockedIndex))

        val newList = list.toMutableList()
        val unlockedIterator = unlockedList.iterator()
        for (i in list.indices) {
            if (crewsLocks[list[i].id] == true) continue
            newList[i] = unlockedIterator.next()
        }

        list = newList
        moveFromIndex = from.index
        moveToIndex = to.index
        haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(vertical = 4.dp),
        state = lazyListState,
        contentPadding = PaddingValues(top = 80.dp, start = 8.dp, end = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        itemsIndexed(list, key = { _, crew -> crew.id }) { index, crew ->
            SwipeToDismissContainer(
                item = crew,
                itemName = crew.name,
                showServiceIcon = true,
                onDismiss = { crewToDismiss, comment, onError ->
                    onCrewDismiss(crewToDismiss, index, comment)
                    list = list.toMutableList().apply { removeAt(index) }
                }
            ) { crewItem ->
                val interactionSource = remember { MutableInteractionSource() }
                val targetImprovement = targetImprovements.find { it.id == crew.id }
                if (crewsLocks[crewItem.id] == false) {
                    ReorderableItem(reorderableLazyColumnState, crew.id) { isDragging ->
                        CrewItem(
                            index = index,
                            list = list,
                            interactionSource = interactionSource,
                            crew = crew,
                            isLocked = false,
                            onLockChange = onLockChange,
                            countsForImprovementMap = countsForImprovementMap,
                            onChangeCountsForImprovementMap = onChangeCountsForImprovementMap,
                            targetImprovement = targetImprovement,
                            isDragging = isDragging,
                            draggingModifier = Modifier
                                .draggableHandle(
                                    onDragStarted = {
                                        haptic.performHapticFeedback(
                                            ReorderHapticFeedbackType.START
                                        )
                                    },
                                    onDragStopped = {
                                        haptic.performHapticFeedback(
                                            ReorderHapticFeedbackType.END
                                        )
                                        if (moveFromIndex != null && moveToIndex != null) {
                                            onCrewMove(list)
                                            val movedCrew = list[moveToIndex!!]
                                            onLockChange(movedCrew.id)
                                            moveFromIndex = null
                                            moveToIndex = null
                                        }
                                    },
                                    interactionSource = interactionSource,
                                )
                                .clearAndSetSemantics { }
                        )
                    }
                } else {
                    CrewItem(
                        index = index,
                        list = list,
                        interactionSource = interactionSource,
                        crew = crew,
                        isLocked = true,
                        onLockChange = onLockChange,
                        countsForImprovementMap = countsForImprovementMap,
                        onChangeCountsForImprovementMap = onChangeCountsForImprovementMap,
                        targetImprovement = targetImprovement,
                        isDragging = false,
                    )
                }

            }
        }
    }
}

@Composable
fun CrewItem(
    index: Int,
    list: List<Crew>,
    interactionSource: MutableInteractionSource?,
    crew: Crew,
    isLocked: Boolean,
    onLockChange: (Int) -> Unit,
    countsForImprovementMap: Map<Int, Boolean>,
    onChangeCountsForImprovementMap: (Int, Boolean) -> Unit,
    targetImprovement: TargetImprovement?,
    isDragging: Boolean,
    draggingModifier: Modifier = Modifier,
) {
    val itemAlpha = if (isLocked) 0.6f else 1f

    ElevatedCard(
        onClick = {},
        modifier = Modifier
            .height(47.dp)
            .padding(horizontal = 20.dp)
            .graphicsLayer(alpha = itemAlpha)
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = "Move Up",
                        action = {
                            if (index > 0) {
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
            defaultElevation = if(isLocked) 0.dp else 2.dp
        ),
        interactionSource = interactionSource,
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                (index + 1).toString(),
                Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.titleMedium,
            )
            IconButton(
                onClick = { onLockChange(crew.id) }
            ) {
                Icon(
                    painter = if (isLocked) painterResource(Res.drawable.lock_key) else painterResource(
                        Res.drawable.lock_key_open
                    ),
                    tint = if(isLocked) MaterialTheme.colorScheme.secondary else Color.Black,
                    contentDescription = stringResource(Res.string.description_lock_un_lock),
                )
            }
            Text(crew.name, Modifier.padding(horizontal = 4.dp))
            Spacer(Modifier.weight(1f))
            IconCheckBox(
                isChecked = countsForImprovementMap[crew.id],
                onCheckedChange = { newValue ->
                    onChangeCountsForImprovementMap(crew.id, newValue)
                },
                discipline = Discipline.Team.BOAT_RACE
            )
            Column(
                modifier = Modifier.padding(horizontal = 4.dp).width(115.dp)
            ) {
                    Text(
                        text = if (targetImprovement?.improvementValue == null || targetImprovement.improvementString == null) {
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
                modifier = draggingModifier,
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