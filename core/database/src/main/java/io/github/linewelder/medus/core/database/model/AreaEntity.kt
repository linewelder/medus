package io.github.linewelder.medus.core.database.model

import androidx.room.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*

@Entity(tableName = "areas")
data class AreaEntity(
    @PrimaryKey
    val id: Id<Area>,
    val name: String,
)

@Dao
interface AreaDao {
    @Query("SELECT * FROM areas ORDER BY name COLLATE NOCASE, id")
    fun getAllAreas(): Flow<List<AreaEntity>>

    @Insert
    suspend fun insert(area: AreaEntity)
}
