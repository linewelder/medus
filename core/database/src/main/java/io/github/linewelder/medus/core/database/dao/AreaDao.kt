package io.github.linewelder.medus.core.database.dao

import androidx.room.*
import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*

@Dao
abstract class AreaDao {
    @Query("SELECT * FROM areas ORDER BY sortOrder, id")
    abstract fun getAll(): Flow<List<AreaEntity>>

    @Query("""
        SELECT  areas.*,
                (SELECT COUNT(*)
                FROM tasks
                WHERE tasks.dateCompleted IS NULL AND tasks.areaId = areas.id
                ) AS uncompletedTaskCount
        FROM areas
        ORDER BY sortOrder, id
        """)
    abstract fun getAllWithTaskCounts(): Flow<List<AreaWithTaskCount>>

    /** Observe areas that have no tasks, including completed tasks. */
    @Query("SELECT * FROM areas WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE tasks.areaId = areas.id)")
    abstract fun getEmpty(): Flow<List<AreaEntity>>

    /** Insert the area and return its assigned sort order. */
    @Transaction
    open suspend fun insert(area: AreaEntity): Long {
        val sortOrder = nextSortOrder()
        insertEntity(area.copy(sortOrder = sortOrder))
        return sortOrder
    }

    @Update
    abstract suspend fun update(areas: List<AreaEntity>)

    @Query("DELETE FROM areas WHERE id = :id")
    abstract suspend fun delete(id: Id<Area>)

    @Transaction
    open suspend fun reorder(id: Id<Area>, newSortOrder: Int) {
        val areas = getAll().first().toMutableList()
        val currentIndex = areas.indexOfFirst { it.id == id }
        val canMove = currentIndex >= 0 && newSortOrder in areas.indices
        if (!canMove) return

        areas.add(newSortOrder, areas.removeAt(currentIndex))
        areas.forEachIndexed { index, area ->
            val sortOrder = index.toLong()
            if (area.sortOrder != sortOrder) updateSortOrder(area.id, sortOrder)
        }
    }

    @Query("UPDATE areas SET sortOrder = :sortOrder WHERE id = :id")
    protected abstract suspend fun updateSortOrder(id: Id<Area>, sortOrder: Long)

    @Query("SELECT COALESCE(MAX(sortOrder) + 1, 0) FROM areas")
    protected abstract suspend fun nextSortOrder(): Long

    @Insert
    protected abstract suspend fun insertEntity(area: AreaEntity)
}

data class AreaWithTaskCount(
    @Embedded val area: AreaEntity,
    val uncompletedTaskCount: Int,
)
