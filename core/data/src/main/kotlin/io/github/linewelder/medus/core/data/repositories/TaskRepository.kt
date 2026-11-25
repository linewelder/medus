package io.github.linewelder.medus.core.data.repositories

import io.github.linewelder.medus.core.data.mappings.*
import io.github.linewelder.medus.core.database.*
import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import kotlin.time.*

class TaskRepository @Inject constructor(
    private val database: MedusDatabase,
) {
    fun findAll() =
        database.taskDao().getAll().map { it.map(TaskEntity::asExternalModel) }

    suspend fun findById(id: Id<Task>) = database.taskDao().getById(id)?.asExternalModel()

    suspend fun insert(task: Task) = database.taskDao().insert(task.asEntity())

    suspend fun update(task: Task) = database.taskDao().update(task.asEntity())

    suspend fun delete(id: Id<Task>) = database.taskDao().delete(id)

    suspend fun setCompleted(id: Id<Task>, completed: Boolean) {
        if (completed) {
            database.taskDao().markCompleted(id, Clock.System.now())
        } else {
            database.taskDao().markIncomplete(id)
        }
    }
}
