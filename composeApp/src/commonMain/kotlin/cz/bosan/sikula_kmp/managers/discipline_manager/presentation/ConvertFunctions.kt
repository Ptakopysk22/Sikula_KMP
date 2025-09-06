package cz.bosan.sikula_kmp.managers.discipline_manager.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.agility
import sikula_kmp.composeapp.generated.resources.badges
import sikula_kmp.composeapp.generated.resources.boat_racing
import sikula_kmp.composeapp.generated.resources.bonuses
import sikula_kmp.composeapp.generated.resources.corrections
import sikula_kmp.composeapp.generated.resources.discipline_badges
import sikula_kmp.composeapp.generated.resources.ghost
import sikula_kmp.composeapp.generated.resources.grenades
import sikula_kmp.composeapp.generated.resources.individual_discipline_agility
import sikula_kmp.composeapp.generated.resources.individual_discipline_grenades
import sikula_kmp.composeapp.generated.resources.individual_discipline_morse
import sikula_kmp.composeapp.generated.resources.individual_discipline_negative_points
import sikula_kmp.composeapp.generated.resources.individual_discipline_night_game
import sikula_kmp.composeapp.generated.resources.individual_discipline_pull_ups
import sikula_kmp.composeapp.generated.resources.individual_discipline_rope_climbing
import sikula_kmp.composeapp.generated.resources.individual_discipline_swimming_race
import sikula_kmp.composeapp.generated.resources.individual_discipline_tidying
import sikula_kmp.composeapp.generated.resources.individual_discipline_trail
import sikula_kmp.composeapp.generated.resources.individual_discipline_trip
import sikula_kmp.composeapp.generated.resources.individual_discipline_unknown
import sikula_kmp.composeapp.generated.resources.morse
import sikula_kmp.composeapp.generated.resources.negative_points
import sikula_kmp.composeapp.generated.resources.night_game
import sikula_kmp.composeapp.generated.resources.points_btc
import sikula_kmp.composeapp.generated.resources.pull_ups
import sikula_kmp.composeapp.generated.resources.quiz
import sikula_kmp.composeapp.generated.resources.rope_climbing
import sikula_kmp.composeapp.generated.resources.sun_horizon
import sikula_kmp.composeapp.generated.resources.swimming_race
import sikula_kmp.composeapp.generated.resources.team_discipline_all
import sikula_kmp.composeapp.generated.resources.team_discipline_boat_race
import sikula_kmp.composeapp.generated.resources.team_discipline_bonuses
import sikula_kmp.composeapp.generated.resources.team_discipline_corrections
import sikula_kmp.composeapp.generated.resources.team_discipline_morning_exercise
import sikula_kmp.composeapp.generated.resources.team_discipline_quiz
import sikula_kmp.composeapp.generated.resources.team_discipline_theme_game
import sikula_kmp.composeapp.generated.resources.team_discipline_unknown
import sikula_kmp.composeapp.generated.resources.theme_game
import sikula_kmp.composeapp.generated.resources.tidying
import sikula_kmp.composeapp.generated.resources.trail
import sikula_kmp.composeapp.generated.resources.trip

@Composable
fun getDisciplineName(discipline: Discipline): String {
    return when (discipline) {
        Discipline.Individual.TRAIL -> stringResource(Res.string.individual_discipline_trail)
        Discipline.Individual.NEGATIVE_POINTS -> stringResource(Res.string.individual_discipline_negative_points)
        Discipline.Individual.ROPE_CLIMBING -> stringResource(Res.string.individual_discipline_rope_climbing)
        Discipline.Individual.PULL_UPS -> stringResource(Res.string.individual_discipline_pull_ups)
        Discipline.Individual.GRENADES -> stringResource(Res.string.individual_discipline_grenades)
        Discipline.Individual.TIDYING -> stringResource(Res.string.individual_discipline_tidying)
        Discipline.Individual.MORSE -> stringResource(Res.string.individual_discipline_morse)
        Discipline.Individual.TRIP -> stringResource(Res.string.individual_discipline_trip)
        Discipline.Individual.SWIMMING_RACE -> stringResource(Res.string.individual_discipline_swimming_race)
        Discipline.Individual.AGILITY -> stringResource(Res.string.individual_discipline_agility)
        Discipline.Individual.NIGHT_GAME -> stringResource(Res.string.individual_discipline_night_game)
        Discipline.Individual.UNKNOWN_DISCIPLINE -> stringResource(Res.string.individual_discipline_unknown)
        Discipline.Team.BOAT_RACE -> stringResource(Res.string.team_discipline_boat_race)
        Discipline.Team.QUIZ -> stringResource(Res.string.team_discipline_quiz)
        Discipline.Team.THEME_GAME -> stringResource(Res.string.team_discipline_theme_game)
        Discipline.Team.BONUSES -> stringResource(Res.string.team_discipline_bonuses)
        Discipline.Team.CORRECTIONS -> stringResource(Res.string.team_discipline_corrections)
        Discipline.Team.UNKNOWN_DISCIPLINE -> stringResource(Res.string.team_discipline_unknown)
        Discipline.Badges.BADGES -> stringResource(Res.string.discipline_badges)
        Discipline.Team.ALL -> stringResource(Res.string.team_discipline_all)
        Discipline.Team.MORNING_EXERCISE -> stringResource(Res.string.team_discipline_morning_exercise)
    }
}

@Composable
fun getDisciplineIcon(discipline: Discipline): Painter {
    return when (discipline) {
        Discipline.Individual.TRAIL -> painterResource(Res.drawable.trail)
        Discipline.Individual.NEGATIVE_POINTS -> painterResource(Res.drawable.negative_points)
        Discipline.Individual.ROPE_CLIMBING -> painterResource(Res.drawable.rope_climbing)
        Discipline.Individual.PULL_UPS -> painterResource(Res.drawable.pull_ups)
        Discipline.Individual.GRENADES -> painterResource(Res.drawable.grenades)
        Discipline.Individual.TIDYING -> painterResource(Res.drawable.tidying)
        Discipline.Individual.MORSE -> painterResource(Res.drawable.morse)
        Discipline.Individual.TRIP -> painterResource(Res.drawable.trip)
        Discipline.Individual.SWIMMING_RACE -> painterResource(Res.drawable.swimming_race)
        Discipline.Individual.AGILITY -> painterResource(Res.drawable.agility)
        Discipline.Individual.NIGHT_GAME -> painterResource(Res.drawable.night_game)
        Discipline.Individual.UNKNOWN_DISCIPLINE -> painterResource(Res.drawable.ghost)
        Discipline.Team.BOAT_RACE -> painterResource(Res.drawable.boat_racing)
        Discipline.Team.QUIZ -> painterResource(Res.drawable.quiz)
        Discipline.Team.THEME_GAME -> painterResource(Res.drawable.theme_game)
        Discipline.Team.BONUSES -> painterResource(Res.drawable.bonuses)
        Discipline.Team.CORRECTIONS -> painterResource(Res.drawable.corrections)
        Discipline.Team.UNKNOWN_DISCIPLINE -> painterResource(Res.drawable.ghost)
        Discipline.Badges.BADGES -> painterResource(Res.drawable.badges)
        Discipline.Team.ALL -> painterResource(Res.drawable.points_btc)
        Discipline.Team.MORNING_EXERCISE -> painterResource(Res.drawable.sun_horizon)
    }
}