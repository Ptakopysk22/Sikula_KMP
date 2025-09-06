package cz.bosan.sikula_kmp.managers.children_manager.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class TrailCategoryEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val campId: Int,
    val name: String,
    val description: String,
    val baseTime: Int,
    val color: String,
)

@Dao
interface TrailCategoryDao {

    @Query("SELECT * FROM TrailCategoryEntity WHERE campId = :campId")
    suspend fun getLocalTrailCategories(campId: Int): List<TrailCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTrailCategories(trailCategories: List<TrailCategoryEntity>)

}