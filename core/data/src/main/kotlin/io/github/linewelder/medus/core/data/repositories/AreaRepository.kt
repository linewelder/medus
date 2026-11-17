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

    suspend fun moveArea(id: Id<Area>, offset: Int) = database.areaDao().moveArea(id, offset)

    suspend fun deleteEmptyArea(id: Id<Area>) = database.areaDao().deleteEmptyArea(id)
}
