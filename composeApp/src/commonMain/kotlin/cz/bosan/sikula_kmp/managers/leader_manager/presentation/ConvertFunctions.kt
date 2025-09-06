package cz.bosan.sikula_kmp.managers.leader_manager.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import cz.bosan.sikula_kmp.managers.discipline_manager.domain.Discipline
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sikula_kmp.composeapp.generated.resources.Res
import sikula_kmp.composeapp.generated.resources.child_leader
import sikula_kmp.composeapp.generated.resources.director
import sikula_kmp.composeapp.generated.resources.game_master_icon
import sikula_kmp.composeapp.generated.resources.ghost
import sikula_kmp.composeapp.generated.resources.guest
import sikula_kmp.composeapp.generated.resources.head_group_leader
import sikula_kmp.composeapp.generated.resources.non_child_leader
import sikula_kmp.composeapp.generated.resources.position_badges_master
import sikula_kmp.composeapp.generated.resources.position_boat_race_master
import sikula_kmp.composeapp.generated.resources.position_morse_master
import sikula_kmp.composeapp.generated.resources.position_negative_points_master
import sikula_kmp.composeapp.generated.resources.position_quiz_master
import sikula_kmp.composeapp.generated.resources.position_unknown_master
import sikula_kmp.composeapp.generated.resources.role_child_leader
import sikula_kmp.composeapp.generated.resources.role_director
import sikula_kmp.composeapp.generated.resources.role_game_master
import sikula_kmp.composeapp.generated.resources.role_guest
import sikula_kmp.composeapp.generated.resources.role_head_group_leader
import sikula_kmp.composeapp.generated.resources.role_no_role
import sikula_kmp.composeapp.generated.resources.role_non_child_leader
import sikula_kmp.composeapp.generated.resources.role_supply
import sikula_kmp.composeapp.generated.resources.supply

@Composable
fun getRoleName(role: Role): String {
    return when (role) {
        Role.CHILD_LEADER -> Res.string.role_child_leader
        Role.HEAD_GROUP_LEADER -> Res.string.role_head_group_leader
        Role.DIRECTOR -> Res.string.role_director
        Role.SUPPLY -> Res.string.role_supply
        Role.GAME_MASTER -> Res.string.role_game_master
        Role.GUEST -> Res.string.role_guest
        Role.NON_CHILD_LEADER -> Res.string.role_non_child_leader
        Role.NO_ROLE -> Res.string.role_no_role
    }.let { stringResource(it) }
}


@Composable
fun getRoleIcon(role: Role?): Painter {
    return when (role) {
        Role.CHILD_LEADER -> Res.drawable.child_leader
        Role.HEAD_GROUP_LEADER -> Res.drawable.head_group_leader
        Role.DIRECTOR -> Res.drawable.director
        Role.SUPPLY -> Res.drawable.supply
        Role.GAME_MASTER -> Res.drawable.game_master_icon
        Role.GUEST -> Res.drawable.guest
        Role.NON_CHILD_LEADER -> Res.drawable.non_child_leader
        Role.NO_ROLE -> Res.drawable.ghost
        null -> Res.drawable.ghost
    }.let { painterResource(it) }
}

@Composable
fun getPositionName(position: Position): String {
    return when (position) {
        Position.BADGES_MASTER -> Res.string.position_badges_master
        Position.NEGATIVE_POINTS_MASTER -> Res.string.position_negative_points_master
        Position.BOAT_RACE_MASTER -> Res.string.position_boat_race_master
        Position.MORSE_MASTER -> Res.string.position_morse_master
        Position.QUIZ_MASTER -> Res.string.position_quiz_master
        //Position.KNOT_MASTER -> Res.string.position_knot_master
        //Position.BANKER -> Res.string.position_banker_master
        //Position.SWIMMING_RACE_MASTER -> Res.string.position_swimming_race_master
        //Position.TRIP_MASTER -> Res.string.position_trip_master
        Position.UNKNOWN_POSITION -> Res.string.position_unknown_master
    }.let { stringResource(it) }
}

@Suppress("SimplifiableCallChain")
@Composable
fun getPositionsName(positions: List<Position>) = positions
    .map { getPositionName(it) }
    .joinToString(", ")

fun getMatchDiscipline(position: Position): Discipline {
    return when (position) {
        Position.BADGES_MASTER -> Discipline.Badges.BADGES
        Position.NEGATIVE_POINTS_MASTER -> Discipline.Individual.NEGATIVE_POINTS
        Position.BOAT_RACE_MASTER -> Discipline.Team.BOAT_RACE
        Position.MORSE_MASTER -> Discipline.Individual.MORSE
        Position.QUIZ_MASTER -> Discipline.Team.QUIZ
        //Position.KNOT_MASTER -> Discipline.Individual.UNKNOWN_DISCIPLINE
        //Position.BANKER ->  Discipline.Individual.UNKNOWN_DISCIPLINE
        //Position.SWIMMING_RACE_MASTER -> Discipline.Individual.SWIMMING_RACE
        //Position.TRIP_MASTER -> Discipline.Individual.TRIP
        Position.UNKNOWN_POSITION -> Discipline.Individual.UNKNOWN_DISCIPLINE
    }
}