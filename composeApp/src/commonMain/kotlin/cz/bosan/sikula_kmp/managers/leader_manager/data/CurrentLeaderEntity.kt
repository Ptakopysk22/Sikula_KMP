package cz.bosan.sikula_kmp.managers.leader_manager.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Position
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Entity
data class CurrentLeaderEntity(
    @PrimaryKey(autoGenerate = false) val id: Long = 1,
    val campId: Int?,
    val campStartDate: LocalDate,
    val campsEndDate: LocalDate,
    val campName: String,
    val leaderId: Int?,
    val name: String?,
    val nickName: String?,
    val email: String?,
    val imageUrl: String?,
    val backendToken: String,
    val backendTokenExpiration: LocalDateTime,
    val refreshToken: String?,
    val refreshTokenExpiration: LocalDateTime,
    val role: Role,
    val isActive: Boolean,
    val groupId: Int?,
    val bankAccount: String?,
    val positions: List<Position>
)

@Dao
interface CurrentLeaderDao {

    @Query("SELECT campId FROM CurrentLeaderEntity WHERE id = 1")
    suspend fun getCurrentCampId(): Int?

    @Query("""
        SELECT julianday(campsEndDate) - julianday(campStartDate) 
        FROM CurrentLeaderEntity 
        WHERE id = 1
    """)
    suspend fun getCampDuration(): Double

    @Query("SELECT * FROM CurrentLeaderEntity WHERE id = 1")
    suspend fun getCurrentLeaderEntity(): CurrentLeaderEntity?

    @Query("SELECT * FROM CurrentLeaderEntity WHERE id = 1 LIMIT 1")
    fun getCurrentLeaderEntityFlow(): Flow<CurrentLeaderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCurrentLeadersCamp(entity: CurrentLeaderEntity)

    @Query("DELETE FROM CurrentLeaderEntity")
    suspend fun deleteCurrentLeadersCamp()
}


