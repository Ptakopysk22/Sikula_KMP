package cz.bosan.sikula_kmp.app

import kotlinx.serialization.Serializable

interface Graph {

    @Serializable
    data object SignIn : Graph

    @Serializable
    data object Home : Graph

    @Serializable
    data object AboutApp : Graph

    @Serializable
    data object AttendeeManager : Graph

    @Serializable
    data class MorningExerciseGraph(val disciplineId: Int? = null, val campDay: Int? = null) : Graph

    @Serializable
    data class PositionDisciplinesGraph(val disciplineId: Int? = null, val campDay: Int? = null) : Graph

    @Serializable
    data class ChildRecordsGraph(val disciplineId: String, val childJson: String) : Graph

    @Serializable
    data class PointsManagementGraph(val disciplineId: Int? = null, val campDay: Int? = null) : Graph
    
    //limo counter graphs

    @Serializable
    data object ConsumerManager : Graph

    @Serializable
    data object ProductManager : Graph

    @Serializable
    data object CashRegister : Graph

}