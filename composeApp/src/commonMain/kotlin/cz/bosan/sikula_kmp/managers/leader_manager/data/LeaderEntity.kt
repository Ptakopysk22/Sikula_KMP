package cz.bosan.sikula_kmp.managers.leader_manager.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import cz.bosan.sikula_kmp.managers.leader_manager.domain.Role

@Entity
data class LeaderEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val campId: Int,
    val name: String?,
    val nickName: String?,
    val role: Role,
    val groupId: Int?,
)

@Dao
interface LeaderDao {

    @Query("SELECT * FROM LeaderEntity WHERE campId = :campId")
    suspend fun getLocalLeaders(campId: Int): List<LeaderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLeaders(leaders: List<LeaderEntity>)
}
