package cz.bosan.sikula_kmp.core.domain

sealed interface Info: Error {
    enum class Common: Info {
        INITIALIZE_TRAIL_RECORDING,
        INITIALIZE_BOAT_RACE_RECORDING,
        COUNT_RECODING,
        COUNT_RECODING_TEAM,
        UNKNOWN
    }
}