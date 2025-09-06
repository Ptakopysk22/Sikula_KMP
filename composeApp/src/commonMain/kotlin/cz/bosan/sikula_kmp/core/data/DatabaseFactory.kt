package cz.bosan.sikula_kmp.core.data

import androidx.room.RoomDatabase

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<LocalDB>
}