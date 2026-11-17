package io.github.linewelder.medus.core.data.repositories

import io.github.linewelder.medus.core.database.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*
import javax.inject.*

/** Read and manage areas independently of task changes. */
class AreaRepository @Inject constructor(
    private val database: MedusDatabase,
) {
    fun findAllAreas(): Flow<List<Area>> =
        database.areaDao().getAllAreas().map { areas -> areas.map { Area(it.id, it.name, it.sortOrder) } }

    fun findEmptyAreaIds(): Flow<Set<Id<Area>>> =
        database.areaDao().getEmptyAreas().map { areas -> areas.map { it.id }.toSet() }

    suspend fun moveArea(id: Id<Area>, newIndex: Int) = database.areaDao().moveArea(id, newIndex)

    suspend fun deleteEmptyArea(id: Id<Area>) = database.areaDao().deleteEmptyArea(id)
}
