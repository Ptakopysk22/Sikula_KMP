package cz.bosan.sikula_kmp.core.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import cz.bosan.sikula_kmp.managers.camp_manager.data.CrewDao
import cz.bosan.sikula_kmp.managers.camp_manager.data.CrewEntity
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildDao
import cz.bosan.sikula_kmp.managers.children_manager.data.ChildEntity
import cz.bosan.sikula_kmp.managers.children_manager.data.TrailCategoryDao
import cz.bosan.sikula_kmp.managers.children_manager.data.TrailCategoryEntity
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.IndividualRecordDao
import cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data.IndividualRecordEntity
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data.TeamRecordDao
import cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data.TeamRecordEntity
import cz.bosan.sikula_kmp.managers.leader_manager.data.CurrentLeaderDao
import cz.bosan.sikula_kmp.managers.leader_manager.data.CurrentLeaderEntity
import cz.bosan.sikula_kmp.managers.leader_manager.data.LeaderDao
import cz.bosan.sikula_kmp.managers.leader_manager.data.LeaderEntity
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object LocalDatabaseConstructor : RoomDatabaseConstructor<LocalDB> {
    override fun initialize(): LocalDB
}

@ConstructedBy(LocalDatabaseConstructor::class)
@TypeConverters(Converters::class)
@Database(
    entities = [CurrentLeaderEntity::class, IndividualRecordEntity::class, ChildEntity::class, TrailCategoryEntity::class, LeaderEntity::class, CrewEntity::class, TeamRecordEntity::class],
    version = 1,
    //autoMigrations = [AutoMigration(from = 1, to =2)]
)

abstract class LocalDB : RoomDatabase() {
    abstract val currentLeaderDao: CurrentLeaderDao
    abstract val individualRecordDao: IndividualRecordDao
    abstract val childDao: ChildDao
    abstract val trailCategoryDao: TrailCategoryDao
    abstract val leaderDao: LeaderDao
    abstract val crewDao: CrewDao
    abstract val teamRecordDao: TeamRecordDao

    companion object {
        const val DB_NAME = "sikula_local.db"
    }
}

class Converters {

    @TypeConverter
    fun fromPositionsList(value: List<Position>): String {
        return value.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toPositionsList(value: String): List<Position> {
        return value.split(",")
            .mapNotNull { Position.entries.find { position -> position.name == it } }
    }


    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString)
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String {
        return dateTime.toString()
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeString)
    }
}