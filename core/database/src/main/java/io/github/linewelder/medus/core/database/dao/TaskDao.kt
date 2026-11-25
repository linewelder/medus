package io.github.linewelder.medus.core.database.dao

import androidx.room.*
import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*
import kotlin.time.*

@Dao
interface TaskDao {
    @Query("""
        SELECT tasks.*
        FROM tasks
        JOIN areas ON areas.id = tasks.areaId
        ORDER BY (dateCompleted IS NOT NULL) ASC,
                 areas.sortOrder ASC,
                 dateCreated DESC,
                 id DESC
        """)
    fun getAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Id<Task>): TaskEntity?

    @Query("UPDATE tasks SET dateCompleted = COALESCE(dateCompleted, :dateCompleted) WHERE id = :id")
    suspend fun markCompleted(id: Id<Task>, dateCompleted: Instant)

    @Query("UPDATE tasks SET dateCompleted = NULL WHERE id = :id")
    suspend fun markIncomplete(id: Id<Task>)

    @Query("""
        UPDATE tasks
        SET title = :title, areaId = :areaId, dateCompleted = :dateCompleted,
            timeEstimateMins = :timeEstimateMins, priceEstimate = :priceEstimate
        WHERE id = :id
        """)
    suspend fun update(
        id: Id<Task>,
        title: String,
        areaId: Id<Area>?,
        dateCompleted: Instant?,
        timeEstimateMins: Int?,
        priceEstimate: Int?,
    )

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun delete(id: Id<Task>)

    @Insert
    suspend fun insert(task: TaskEntity)
}
