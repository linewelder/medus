package io.github.linewelder.medus.core.database.dao

import androidx.room.*
import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*

@Dao
interface AreaDao {
    @Query("SELECT * FROM areas ORDER BY sortOrder, id")
    fun getAllAreas(): Flow<List<AreaEntity>>

    /** Observe areas that have no tasks, including completed tasks. */
    @Query("SELECT * FROM areas WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE tasks.areaId = areas.id)")
    fun getEmptyAreas(): Flow<List<AreaEntity>>

    /** Insert an area at the end of the current order and assign it the saved position. */
    @Transaction
    suspend fun insert(area: Area) {
        val sortOrder = nextSortOrder()
        insertEntity(AreaEntity(area.id, area.name, sortOrder))
        area.sortOrder = sortOrder
    }

    @Transaction
    suspend fun moveArea(id: Id<Area>, newIndex: Int) {
        val areas = getAllAreas().first().toMutableList()
        val currentIndex = areas.indexOfFirst { it.id == id }
        val canMove = currentIndex >= 0 && newIndex in areas.indices
        if (!canMove) return

        areas.add(newIndex, areas.removeAt(currentIndex))
        areas.forEachIndexed { index, area ->
            val sortOrder = index.toLong()
            if (area.sortOrder != sortOrder) updateSortOrder(area.id, sortOrder)
        }
    }

    @Query("UPDATE areas SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateSortOrder(id: Id<Area>, sortOrder: Long)

    /** Delete an area only if no task references it. */
    @Query("DELETE FROM areas WHERE id = :id AND NOT EXISTS (SELECT 1 FROM tasks WHERE areaId = :id)")
    suspend fun deleteEmptyArea(id: Id<Area>)

    @Query("SELECT COALESCE(MAX(sortOrder) + 1, 0) FROM areas")
    suspend fun nextSortOrder(): Long

    @Insert
    suspend fun insertEntity(area: AreaEntity)
}
