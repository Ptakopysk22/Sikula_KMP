package cz.bosan.sikula_kmp.managers.discipline_manager.teamDisciplineManager.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.datetime.LocalDateTime

@Entity
data class TeamRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val idOnServer: Int?,
    val campId: Int,
    val campDay: Int,
    val disciplineId: Int,
    val crewId: Int,
    val value: String?,
    val timeStamp: LocalDateTime,
    val isUploaded: Boolean,
    val refereeId: Int,
    val comment: String,
    val countsForImprovement: Boolean?,
    val improvement: String?,
    val isRecord: Boolean?,
)

@Dao
interface TeamRecordDao {

    @Query("SELECT * FROM TeamRecordEntity WHERE campId = :campId AND campDay = :campDay AND disciplineId = :disciplineId")
    suspend fun getLocalTeamDayRecords(campId: Int, campDay: Int, disciplineId: Int): List<TeamRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(entity: TeamRecordEntity): Long

}