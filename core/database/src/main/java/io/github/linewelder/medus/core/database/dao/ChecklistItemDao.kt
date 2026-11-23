package io.github.linewelder.medus.core.database.dao

import androidx.room.*
import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*
import kotlin.time.*

@Dao
abstract class ChecklistItemDao {
    /** Append an incomplete item to its checklist. */
    @Query(
        """
        INSERT INTO checklist_items (id, checklistId, title, position)
        SELECT :id, :checklistId, :title, COALESCE(MAX(position) + 1, 0)
        FROM checklist_items WHERE checklistId = :checklistId
        """,
    )
    abstract suspend fun insert(id: Id<ChecklistItem>, checklistId: Id<Checklist>, title: String)

    @Query("DELETE FROM checklist_items WHERE id = :id")
    abstract suspend fun delete(id: Id<ChecklistItem>)

    @Query("UPDATE checklist_items SET title = :title WHERE id = :id")
    abstract suspend fun update(id: Id<ChecklistItem>, title: String)

    @Query("UPDATE checklist_items SET dateCompleted = :dateCompleted WHERE id = :id")
    abstract suspend fun setCompletion(id: Id<ChecklistItem>, dateCompleted: Instant?)

    @Transaction
    open suspend fun move(checklistId: Id<Checklist>, id: Id<ChecklistItem>, newIndex: Int) {
        val items = getInChecklist(checklistId).toMutableList()
        val currentIndex = items.indexOfFirst { it.id == id }
        val canMove = currentIndex >= 0 && newIndex in items.indices
        if (!canMove) return

        items.add(newIndex, items.removeAt(currentIndex))
        items.forEachIndexed { position, item ->
            if (item.position != position) updatePosition(item.id, position)
        }
    }

    @Query("SELECT * FROM checklist_items WHERE checklistId = :checklistId ORDER BY position, id")
    protected abstract suspend fun getInChecklist(checklistId: Id<Checklist>): List<ChecklistItemEntity>

    @Query("UPDATE checklist_items SET position = :position WHERE id = :id")
    protected abstract suspend fun updatePosition(id: Id<ChecklistItem>, position: Int)
}
