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
    private val database: MedusDatabase,
    private val areaRepository: AreaRepository,
) {
    fun findAll() =
        database.taskDao().getAll().map { it.map(TaskEntity::asExternalModel) }

    suspend fun findById(id: Id<Task>) = database.taskDao().getById(id)?.asExternalModel()

    /** Update the task and save its new area together. Return whether the task still exists. */
    suspend fun updateWithArea(
        task: Task,
        completed: Boolean,
        newArea: Area?,
    ): Boolean = database.withTransaction {
        val dao = database.taskDao()
        val existing = dao.getById(task.id) ?: return@withTransaction false
        if (newArea != null) areaRepository.insert(newArea)

        val dateCompleted = if (completed) existing.dateCompleted ?: Clock.System.now() else null
        dao.update(
            task.asEntity().copy(
                dateCreated = existing.dateCreated,
                dateCompleted = dateCompleted,
                areaId = newArea?.id ?: task.areaId,
            )
        )
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
    suspend fun createWithArea(task: Task, newArea: Area?) {
        val sortOrder = database.withTransaction {
            val newAreaSortOrder = newArea?.let { areaRepository.insert(it) }
            database.taskDao().insert(task.asEntity().copy(areaId = newArea?.id ?: task.areaId))
            newAreaSortOrder
        }
        if (newArea != null && sortOrder != null) {
            newArea.sortOrder = sortOrder
        }
    }
}
