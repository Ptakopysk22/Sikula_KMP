package cz.bosan.sikula_kmp.managers.points_manager.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PointRecordDto(
    @SerialName("crewID") val crewId: Int,
    @SerialName("disciplineID") val disciplineId: Int?,
    @SerialName("description")  val description: String,
    @SerialName("day") val campDay: Int,
    @SerialName("points") val value: Double,
)

@Serializable
data class AllPointRecordDto(
    @SerialName("totalPoints") val totalPoints: List<PointRecordDto>,
    @SerialName("morningExercise") val morningExercise: List<PointRecordDto>,
    @SerialName("pullUps")  val pullUps: List<PointRecordDto>,
    @SerialName("grenades") val grenades: List<PointRecordDto>,
    @SerialName("cleaning") val tidying: List<PointRecordDto>,
    @SerialName("trail") val trail: List<PointRecordDto>,
    @SerialName("ropeClimbing") val ropeClimbing: List<PointRecordDto>,
    @SerialName("badges")  val badges: List<PointRecordDto>,
    @SerialName("morse")  val morse: List<PointRecordDto>,
    @SerialName("negativePoints") val negativePoints: List<PointRecordDto>,
    @SerialName("an") val boatRace: List<PointRecordDto>,
    @SerialName("cth") val themeGame: List<PointRecordDto>,
    @SerialName("quizz") val quiz: List<PointRecordDto>,
    @SerialName("bonuses") val bonuses: List<PointRecordDto>,
    @SerialName("corrections") val corrections: List<PointRecordDto>,
)

@Serializable
data class MorningExerciseOnlyDto(
    @SerialName("morningExercise") val morningExercise: List<PointRecordDto>,
)