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
    val completed: Boolean,
    val areaId: Id<Area>? = null,
)

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY completed ASC, dateCreated DESC, id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET completed = :completed WHERE id = :id")
    suspend fun setTaskCompleted(id: Id<Task>, completed: Boolean)

    @Insert
    suspend fun insert(task: TaskEntity)
}
