package io.github.linewelder.medus.core.data.repositories

import io.github.linewelder.medus.core.data.mappings.*
import io.github.linewelder.medus.core.database.*
import io.github.linewelder.medus.core.database.model.*
import kotlinx.coroutines.flow.*
import javax.inject.*

class TaskRepository @Inject constructor(
    private val database: MedusDatabase
) {
    fun findAllTasks() =
        database.taskDao().getAllTasks().map { it.map(TaskEntity::asExternalModel) }
}
