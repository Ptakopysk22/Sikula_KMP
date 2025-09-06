package cz.bosan.sikula_kmp.managers.discipline_manager.domain

import androidx.compose.ui.graphics.Color
import cz.bosan.sikula_kmp.core.domain.SelectableItem

sealed interface Discipline : SelectableItem {

    fun getId(): String
    fun getColor(): Color
    fun getResultOptions(): List<String?>

    enum class Individual(override val id: Int) : Discipline {
        TRAIL(200),
        NEGATIVE_POINTS(201),
        ROPE_CLIMBING(202),
        PULL_UPS(203),
        GRENADES(204),
        TIDYING(205),
        MORSE(206),
        TRIP(207),
        SWIMMING_RACE(208),
        AGILITY(209),
        NIGHT_GAME(210),
        UNKNOWN_DISCIPLINE(211);

        override fun getId(): String {
            return this.id.toString()
        }

        override fun getColor(): Color {
            return when (this) {
                TRAIL -> Color(0xFFFFEC1C)
                NEGATIVE_POINTS -> Color(0xFFFF1CC6)
                ROPE_CLIMBING -> Color(0xFF1CFF1C)
                PULL_UPS -> Color(0xFFAC1CFF)
                GRENADES -> Color(0xFF787871)
                TIDYING -> Color(0xFFFFFFFF)
                MORSE -> Color(0xFFB7FF1C)
                TRIP -> Color(0xFFE90202)
                SWIMMING_RACE ->  Color(0xFF1CE8FF)
                AGILITY ->  Color(0xFFF66206)
                NIGHT_GAME ->  Color(0xFF000000)
                else -> Color.Black
            }
        }

        override fun getResultOptions(): List<String?> {
            return when (this) {
                TRAIL -> listOf()
                NEGATIVE_POINTS -> listOf()
                ROPE_CLIMBING -> listOf("0", "1")
                PULL_UPS -> listOf()
                GRENADES -> listOf("0", "1", "2", "3")
                TIDYING -> listOf("0", "1", "2", "3")
                MORSE -> listOf("0", "0,5", "1", "2")
                AGILITY -> listOf("0", "1", "2", "3")
                else -> listOf()
            }
        }

    }

    enum class Team(override val id: Int) : Discipline {
        BOAT_RACE(100),
        QUIZ(101),
        THEME_GAME(102),
        BONUSES(103),
        CORRECTIONS(104),
        ALL(199),
        MORNING_EXERCISE(198),
        UNKNOWN_DISCIPLINE(105);

        override fun getId(): String {
            return this.id.toString()
        }

        override fun getColor(): Color {
            return when (this) {
                BOAT_RACE -> Color(0xFF4D1CFF)
                QUIZ -> Color(0xFF1CFFA4)
                THEME_GAME -> Color(0xFFE90202)
                BONUSES -> Color(0xFF1CE8FF)
                CORRECTIONS -> Color(0xFF1CFFA4)
                ALL -> Color(0xFFAC1CFF)
                else -> Color.Black
            }
        }

        override fun getResultOptions(): List<String?> {
            return when (this) {
                BOAT_RACE -> listOf()
                QUIZ -> listOf("0", "1", "2", "3", "4", "5")
                THEME_GAME -> listOf()
                BONUSES -> listOf()
                CORRECTIONS -> listOf()
                else -> listOf()
            }
        }
    }

    enum class Badges(override val id: Int) : Discipline {
        BADGES(300);

        override fun getId(): String {
            return this.id.toString()
        }

        override fun getColor(): Color {
            return when (this) {
                BADGES -> Color(0xFF732402)
            }
        }

        override fun getResultOptions(): List<String?> {
            return when (this) {
                BADGES -> listOf()
            }
        }
    }

}

fun getDisciplineById(id: String?): Discipline {

    Discipline.Individual.entries.find { it.getId() == id }?.let {
        return it
    }

    Discipline.Team.entries.find { it.getId() == id }?.let {
        return it
    }

    Discipline.Badges.entries.find { it.getId() == id }?.let {
        return it
    }

    return Discipline.Individual.UNKNOWN_DISCIPLINE
}


