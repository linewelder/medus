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

    fun findAllAreas(): Flow<List<Area>> =
        database.areaDao().getAllAreas().map { areas -> areas.map { Area(it.id, it.name) } }

    /** Save a task and insert its new area in the same transaction when one is provided. */
    suspend fun createTask(title: String, area: Area, isNewArea: Boolean) {
        database.withTransaction {
            if (isNewArea) {
                database.areaDao().insert(AreaEntity(id = area.id, name = area.name))
            }
            database.taskDao().insert(
                TaskEntity(
                    id = Id.random(),
                    title = title,
                    dateCreated = Date(),
                    completed = false,
                    areaId = area.id,
                )
            )
        }
    }
}
