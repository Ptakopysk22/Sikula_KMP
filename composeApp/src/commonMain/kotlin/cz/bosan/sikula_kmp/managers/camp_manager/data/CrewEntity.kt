package cz.bosan.sikula_kmp.managers.camp_manager.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class CrewEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val campId: Int,
    val groupId: Int,
    val name: String,
    val color: String,
)

@Dao
interface CrewDao {

    @Query("SELECT * FROM CrewEntity WHERE campId = :campId")
    suspend fun getLocalCrews(campId: Int): List<CrewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCrews(leaders: List<CrewEntity>)
}