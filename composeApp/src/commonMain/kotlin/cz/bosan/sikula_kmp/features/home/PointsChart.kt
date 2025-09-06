package cz.bosan.sikula_kmp.features.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.dp
import cz.bosan.sikula_kmp.core.presentation.UiText
import cz.bosan.sikula_kmp.core.presentation.components.CampDayCarousel
import cz.bosan.sikula_kmp.core.presentation.components.ContainerBox
import cz.bosan.sikula_kmp.core.presentation.components.Message
import cz.bosan.sikula_kmp.core.presentation.components.MessageTyp
import cz.bosan.sikula_kmp.managers.camp_manager.domain.Crew
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import cz.bosan.sikula_kmp.managers.points_manager.domain.PointRecord
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.all_points

@Composable
fun PointsChart(
    crews: List<Crew>,
    pointRecords: List<PointRecord>,
    warningMessage: UiText?,
    selectedDate: Int,
    campDuration: Int,
    onDayChange: (Int) -> Unit,
    role: Role,
    modifier: Modifier = Modifier,
) {
    val chartData = remember(crews, pointRecords) {
        crews.mapNotNull { crew ->
            val record = pointRecords.find { it.crewId == crew.id && it.value != null }

            record?.let {
                CrewWithValue(
                    crew = crew,
                    value = it.value!!
                )
            }
        }
            .sortedByDescending { it.value }
            .map { crewWithValue ->
                Bars(
                    label = crewWithValue.crew.name,
                    values = listOf(
                        Bars.Data(
                            label = crewWithValue.crew.name,
                            value = crewWithValue.value,
                            color = SolidColor(crewWithValue.crew.color)
                        )
                    )
                )
            }
    }

    val fontFamilyResolver = LocalFontFamilyResolver.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val textMeasurer = remember {
        TextMeasurer(
            defaultFontFamilyResolver = fontFamilyResolver,
            defaultDensity = density,
            defaultLayoutDirection = layoutDirection,
            cacheSize = 32
        )
    }

    ContainerBox(modifier = modifier,
        title = stringResource(Res.string.all_points),
        content = {
            if (warningMessage != null) {
                Message(
                    text = warningMessage.asString(),
                    messageTyp = MessageTyp.WARNING
                )
            } else {
                if (role == Role.GAME_MASTER || role == Role.DIRECTOR) {
                    CampDayCarousel(
                        campDay = selectedDate,
                        campDuration = campDuration,
                        changeWithoutAnimation = {},
                        onDayChanged = { onDayChange(it) },
                        // modifier = Modifier.height(25.dp)
                    )
                }
                RowChart(
                    modifier = Modifier.fillMaxSize().padding(top = 4.dp),
                    data = chartData,
                    barProperties = BarProperties(
                        cornerRadius = Bars.Data.Radius.Rectangle(
                            topRight = 6.dp,
                            bottomRight = 6.dp
                        ),
                        spacing = 8.dp,
                        thickness = 24.dp
                    ),
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    labelHelperProperties = LabelHelperProperties(enabled = false),
                    textMeasurer = textMeasurer
                )
            }
        })
}

private data class CrewWithValue(
    val crew: Crew,
    val value: Double
)