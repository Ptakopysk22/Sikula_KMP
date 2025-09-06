package cz.bosan.sikula_kmp.managers.leader_manager.domain

import kotlinx.datetime.LocalDate

data class Leader(
    val id: Int,
    val name: String,
    val nickName: String,
    val mail: String,
    var role: Role = Role.NO_ROLE,
    val positions: List<Position> = emptyList(),
    val birthDate: LocalDate?,
    val isActive: Boolean,
    val groupId: Int?,
    val bankAccount: String?,
    val occupations: List<Occupation>,
) {
    companion object {
        val EMPTY = Leader(
            name = "",
            nickName = "Neznámý",
            mail = "",
            role = Role.NO_ROLE,
            positions = emptyList(),
            birthDate = null,
            isActive = false,
            groupId = null,
            occupations = emptyList(),
            bankAccount = null,
            id = 0
        )
    }
}

data class Occupation(
    val campId: Int,
    val role: Role,
    val isActive: Boolean,
    val groupId: Int?,
    val positions: List<Position>,
)

enum class Role(val index: Int) {
    DIRECTOR(0),
    HEAD_GROUP_LEADER(1),
    CHILD_LEADER(2),
    GAME_MASTER(3),
    SUPPLY(4),
    NON_CHILD_LEADER(5),
    GUEST(6),
    NO_ROLE(7)
}

enum class Position(val index: Int) {
    BADGES_MASTER(0),
    NEGATIVE_POINTS_MASTER(1),
    BOAT_RACE_MASTER(2),
    MORSE_MASTER(3),
    QUIZ_MASTER(4),
    //KNOT_MASTER(5),
    //BANKER(6),
    //SWIMMING_RACE_MASTER(7),
    //TRIP_MASTER(8),
    UNKNOWN_POSITION(9)
}
