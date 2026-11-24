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
    fun findAll() =
        database.taskDao().getAll().map { it.map(TaskEntity::asExternalModel) }

    suspend fun findById(id: Id<Task>) = database.taskDao().getById(id)?.asExternalModel()

    /** Update the task and save its new area together. Return whether the task still exists. */
    suspend fun update(
        id: Id<Task>,
        title: String,
        areaId: Id<Area>?,
        completed: Boolean,
        newAreaName: String?,
        timeEstimateMins: Int?,
    ): Boolean = database.withTransaction {
        val task = database.taskDao().getById(id) ?: return@withTransaction false

        val newArea = newAreaName?.let { Area(id = Id.random(), name = it) }
        if (newArea != null) {
            database.areaDao().insert(newArea)
        }
        val dateCompleted = if (completed) task.dateCompleted ?: Clock.System.now() else null
        database.taskDao().update(id, title, newArea?.id ?: areaId, dateCompleted, timeEstimateMins)
        true
    }

    suspend fun delete(id: Id<Task>) = database.taskDao().delete(id)

    suspend fun setCompleted(id: Id<Task>, completed: Boolean) {
        if (completed) {
            database.taskDao().markCompleted(id, Clock.System.now())
        } else {
            database.taskDao().markIncomplete(id)
        }
    }

    /** Save a task and insert its new area in the same transaction when one is provided. */
    suspend fun createWithArea(
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
