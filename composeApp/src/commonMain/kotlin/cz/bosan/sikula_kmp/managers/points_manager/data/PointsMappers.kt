package cz.bosan.sikula_kmp.managers.points_manager.data

import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.points_manager.domain.PointRecord
import kotlin.math.round

fun PointRecordDto.toPointRecord(discipline: Discipline): PointRecord {
    return PointRecord(
        crewId = crewId,
        disciplineId = disciplineId
            ?: discipline.id,
        description = description,
        campDay = campDay,
        value = round(value * 10) / 10.0
    )
}

fun AllPointRecordDto.toPointRecords(): List<PointRecord> {
    return totalPoints.map { it.toPointRecord(Discipline.Team.ALL) } +
            morningExercise.map { it.toPointRecord(Discipline.Team.MORNING_EXERCISE) } +
            badges.map { it.toPointRecord(Discipline.Badges.BADGES) } +
            negativePoints.map { it.toPointRecord(Discipline.Individual.NEGATIVE_POINTS) } +
            morse.map { it.toPointRecord(Discipline.Individual.MORSE) } +
            boatRace.map { it.toPointRecord(Discipline.Team.BOAT_RACE) } +
            themeGame.map { it.toPointRecord(Discipline.Team.THEME_GAME) } +
            quiz.map { it.toPointRecord(Discipline.Team.QUIZ) } +
            grenades.map { it.toPointRecord(Discipline.Individual.GRENADES) } +
            ropeClimbing.map { it.toPointRecord(Discipline.Individual.ROPE_CLIMBING) } +
            pullUps.map { it.toPointRecord(Discipline.Individual.PULL_UPS) } +
            trail.map { it.toPointRecord(Discipline.Individual.TRAIL) } +
            tidying.map { it.toPointRecord(Discipline.Individual.TIDYING) } +
            bonuses.map { it.toPointRecord(Discipline.Team.BONUSES) } +
            corrections.map { it.toPointRecord(Discipline.Team.CORRECTIONS) }
}

fun MorningExerciseOnlyDto.toPointRecords(): List<PointRecord> {
    return morningExercise.map { dto -> dto.toPointRecord(Discipline.Team.MORNING_EXERCISE) }
}