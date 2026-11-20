package io.github.linewelder.medus.core.data.repositories

import androidx.room.*
import io.github.linewelder.medus.core.data.mappings.*
import io.github.linewelder.medus.core.database.*
import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import kotlin.time.*

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
        timeEstimateMins: Int?,
    ): Boolean = database.withTransaction {
        val task = database.taskDao().getTask(id) ?: return@withTransaction false

        val newArea = newAreaName?.let { Area(id = Id.random(), name = it) }
        if (newArea != null) {
            database.areaDao().insert(newArea)
        }
        val dateCompleted = if (completed) task.dateCompleted ?: Clock.System.now() else null
        database.taskDao().updateTask(id, title, newArea?.id ?: areaId, dateCompleted, timeEstimateMins)
        true
    }

    suspend fun deleteTask(id: Id<Task>) = database.taskDao().deleteTask(id)

    suspend fun setTaskCompleted(id: Id<Task>, completed: Boolean) {
        if (completed) {
            database.taskDao().markTaskCompleted(id, Clock.System.now())
        } else {
            database.taskDao().markTaskIncomplete(id)
        }
    }

    /** Save a task and insert its new area in the same transaction when one is provided. */
    suspend fun createTask(
        title: String,
        area: Area,
        isNewArea: Boolean,
        timeEstimateMins: Int?,
    ) {
        val newArea = if (isNewArea) area.copy() else null
        database.withTransaction {
            if (newArea != null) {
                database.areaDao().insert(newArea)
            }
            database.taskDao().insert(
                TaskEntity(
                    id = Id.random(),
                    title = title,
                    dateCreated = Clock.System.now(),
                    dateCompleted = null,
                    areaId = area.id,
                    timeEstimateMins = timeEstimateMins,
                )
            )
        }
        if (newArea != null) {
            area.sortOrder = newArea.sortOrder
        }
    }
}
