package io.github.linewelder.medus.core.database.model

import androidx.room.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*

@Entity(tableName = "areas")
data class AreaEntity(
    @PrimaryKey
    val id: Id<Area>,
    val name: String,
    @ColumnInfo(defaultValue = "0")
    val sortOrder: Long,
)

@Dao
interface AreaDao {
    @Query("SELECT * FROM areas ORDER BY sortOrder, id")
    fun getAllAreas(): Flow<List<AreaEntity>>

    /** Insert an area at the end of the current order and assign it the saved position. */
    @Transaction
    suspend fun insert(area: Area) {
        val sortOrder = nextSortOrder()
        insertEntity(AreaEntity(area.id, area.name, sortOrder))
        area.sortOrder = sortOrder
    }

    @Query("SELECT COALESCE(MAX(sortOrder) + 1, 0) FROM areas")
    suspend fun nextSortOrder(): Long

    @Insert
    suspend fun insertEntity(area: AreaEntity)
}
