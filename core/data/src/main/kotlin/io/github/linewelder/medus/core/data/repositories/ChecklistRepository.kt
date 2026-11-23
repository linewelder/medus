package io.github.linewelder.medus.core.data.repositories

import io.github.linewelder.medus.core.data.mappings.*
import io.github.linewelder.medus.core.database.*
import io.github.linewelder.medus.core.database.dao.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import kotlin.time.*

class ChecklistRepository @Inject constructor(
    database: MedusDatabase,
) {
    private val dao = database.checklistDao()
    private val itemDao = database.checklistItemDao()

    fun findAll(): Flow<List<Checklist>> =
        dao.getAll().map { checklists -> checklists.map(ChecklistWithItems::asExternalModel) }

    fun findByRole(role: ChecklistRole): Flow<Checklist?> =
        dao.getByRole(role).map { it?.asExternalModel() }

    fun findById(id: Id<Checklist>): Flow<Checklist?> =
        dao.getById(id).map { it?.asExternalModel() }

    suspend fun addItem(checklistId: Id<Checklist>, title: String) =
        itemDao.insert(Id.random(), checklistId, title)

    suspend fun removeItem(id: Id<ChecklistItem>) = itemDao.delete(id)

    suspend fun updateItem(id: Id<ChecklistItem>, title: String) = itemDao.update(id, title)

    suspend fun setItemCompleted(id: Id<ChecklistItem>, dateCompleted: Instant?) =
        itemDao.setCompletion(id, dateCompleted)

    suspend fun moveItem(checklistId: Id<Checklist>, itemId: Id<ChecklistItem>, newIndex: Int) =
        itemDao.move(checklistId, itemId, newIndex)
}
