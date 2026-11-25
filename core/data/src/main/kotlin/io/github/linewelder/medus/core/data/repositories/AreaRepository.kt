package io.github.linewelder.medus.core.data.repositories

import io.github.linewelder.medus.core.data.mappings.*
import io.github.linewelder.medus.core.database.*
import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*
import javax.inject.*

/** Read and manage areas independently of task changes. */
class AreaRepository @Inject constructor(
    private val database: MedusDatabase,
) {
    fun findAll(): Flow<List<Area>> =
        database.areaDao().getAll().map { areas -> areas.map(AreaEntity::asExternalModel) }

    fun findAllWithTaskCounts(): Flow<List<AreaWithTaskCount>> =
        database.areaDao().getAllWithTaskCounts().map { areas ->
            areas.map { AreaWithTaskCount(it.area.asExternalModel(), it.uncompletedTaskCount) }
        }

    fun findEmptyIds(): Flow<Set<Id<Area>>> =
        database.areaDao().getEmpty().map { areas -> areas.map { it.id }.toSet() }

    suspend fun insert(area: Area): Long = database.areaDao().insert(area.asEntity())

    suspend fun reorder(id: Id<Area>, newSortOrder: Int) = database.areaDao().reorder(id, newSortOrder)

    suspend fun delete(id: Id<Area>) = database.areaDao().delete(id)
}

data class AreaWithTaskCount(val area: Area, val uncompletedTaskCount: Int)
