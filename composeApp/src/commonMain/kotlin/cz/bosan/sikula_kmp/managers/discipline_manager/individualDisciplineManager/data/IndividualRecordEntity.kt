package cz.bosan.sikula_kmp.managers.discipline_manager.individualDisciplineManager.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.datetime.LocalDateTime

@Entity
data class IndividualRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val idOnServer: Int?,
    val campId: Int,
    val disciplineId: Int,
    val competitorId: Int,
    val value: String?,
    val campDay: Int,
    val timeStamp: LocalDateTime,
    val isUploaded: Boolean,
    val refereeId: Int,
    val comment: String,
    val countsForImprovement: Boolean?,
    val improvement: String?,
    val isRecord: Boolean?,
)

@Dao
interface IndividualRecordDao {

    @Query("SELECT * FROM IndividualRecordEntity WHERE campId = :campId AND campDay = :campDay AND disciplineId = :disciplineId")
    suspend fun getLocalIndividualRecords(campId: Int, campDay: Int, disciplineId: Int): List<IndividualRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(entity: IndividualRecordEntity): Long

}