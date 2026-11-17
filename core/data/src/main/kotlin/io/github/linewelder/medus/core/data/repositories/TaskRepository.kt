package io.github.linewelder.medus.core.data.repositories

import androidx.room.*
import io.github.linewelder.medus.core.data.mappings.*
import io.github.linewelder.medus.core.database.*
import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.*

class TaskRepository @Inject constructor(
    private val database: MedusDatabase
) {
    fun findAllTasks() =
        database.taskDao().getAllTasks().map { it.map(TaskEntity::asExternalModel) }

    suspend fun getTask(id: Id<Task>) = database.taskDao().getTask(id)?.asExternalModel()

    /** Update the task and save its new area together. Return whether the task still exists. */
    suspend fun updateTask(
        id: Id<Task>,
        title: String,
        areaId: Id<Area>?,
        completed: Boolean,
        newAreaName: String?,
    ): Boolean = database.withTransaction {
        val task = database.taskDao().getTask(id) ?: return@withTransaction false

        val newArea = newAreaName?.let { Area(id = Id.random(), name = it) }
        if (newArea != null) {
            database.areaDao().insert(newArea)
        }
        val dateCompleted = if (completed) task.dateCompleted ?: Date() else null
        database.taskDao().updateTask(id, title, newArea?.id ?: areaId, dateCompleted)
        true
    }

    suspend fun deleteTask(id: Id<Task>) = database.taskDao().deleteTask(id)

    suspend fun setTaskCompleted(id: Id<Task>, completed: Boolean) {
        if (completed) {
            database.taskDao().markTaskCompleted(id, Date())
        } else {
            database.taskDao().markTaskIncomplete(id)
        }
    }

    fun findAllAreas(): Flow<List<Area>> =
        database.areaDao().getAllAreas().map { areas -> areas.map { Area(it.id, it.name, it.sortOrder) } }

    /** Save a task and insert its new area in the same transaction when one is provided. */
    suspend fun createTask(title: String, area: Area, isNewArea: Boolean) {
        val newArea = if (isNewArea) area.copy() else null
        database.withTransaction {
            if (newArea != null) {
                database.areaDao().insert(newArea)
            }
            database.taskDao().insert(
                TaskEntity(
                    id = Id.random(),
                    title = title,
                    dateCreated = Date(),
                    dateCompleted = null,
                    areaId = area.id,
                )
            )
        }
        if (newArea != null) {
            area.sortOrder = newArea.sortOrder
        }
    }
}
