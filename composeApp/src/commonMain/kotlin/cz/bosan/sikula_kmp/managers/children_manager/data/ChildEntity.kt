package cz.bosan.sikula_kmp.managers.children_manager.data


import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import cz.bosan.sikula_kmp.managers.children_manager.domain.ChildRole
import kotlinx.datetime.LocalDate

@Entity
data class ChildEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val campId: Int,
    val name: String,
    val nickName: String,
    val birthDate: LocalDate?,
    val role: ChildRole?,
    val isActive: Boolean,
    val groupId: Int?,
    val crewId: Int?,
    val trailCategoryId: Int?,
)

@Dao
interface ChildDao {

    @Query("SELECT * FROM ChildEntity WHERE campId = :campId AND groupId = :groupId")
    suspend fun getLocalChildren(campId: Int, groupId: Int): List<ChildEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateChildren(children: List<ChildEntity>)

}