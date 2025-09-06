package cz.bosan.sikula_kmp.core.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<LocalDB> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(LocalDB.DB_NAME)

        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath,
        )
    }
}