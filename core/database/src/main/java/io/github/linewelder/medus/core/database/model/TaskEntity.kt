package io.github.linewelder.medus.core.database.model

import androidx.room.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*
import java.util.*

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = AreaEntity::class,
            parentColumns = ["id"],
            childColumns = ["areaId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("areaId")],
)
data class TaskEntity(
    @PrimaryKey
    val id: Id<Task>,
    val title: String,
    val dateCreated: Date,
    val dateCompleted: Date?,
    val areaId: Id<Area>? = null,
)

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY (dateCompleted IS NOT NULL) ASC, dateCreated DESC, id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTask(id: Id<Task>): TaskEntity?

    @Query("UPDATE tasks SET dateCompleted = COALESCE(dateCompleted, :dateCompleted) WHERE id = :id")
    suspend fun markTaskCompleted(id: Id<Task>, dateCompleted: Date)

    @Query("UPDATE tasks SET dateCompleted = NULL WHERE id = :id")
    suspend fun markTaskIncomplete(id: Id<Task>)

    @Query("UPDATE tasks SET title = :title, areaId = :areaId, dateCompleted = :dateCompleted WHERE id = :id")
    suspend fun updateTask(id: Id<Task>, title: String, areaId: Id<Area>?, dateCompleted: Date?)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: Id<Task>)

    @Insert
    suspend fun insert(task: TaskEntity)
}
