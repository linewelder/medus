package io.github.linewelder.medus.core.database.dao

import androidx.room.*
import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.flow.*

@Dao
interface ChecklistDao {
    @Transaction
    @Query("SELECT * FROM checklists ORDER BY dateCreated, id")
    fun getAll(): Flow<List<ChecklistWithItems>>

    @Transaction
    @Query("SELECT * FROM checklists WHERE role = :role")
    fun getByRole(role: ChecklistRole): Flow<ChecklistWithItems?>

    @Transaction
    @Query("SELECT * FROM checklists WHERE id = :id")
    fun getById(id: Id<Checklist>): Flow<ChecklistWithItems?>

    @Query("SELECT EXISTS(SELECT 1 FROM checklists WHERE role = :role)")
    suspend fun existsByRole(role: ChecklistRole): Boolean

    @Insert
    suspend fun insert(checklist: ChecklistEntity)
}

data class ChecklistWithItems(
    @Embedded val checklist: ChecklistEntity,
    @Relation(parentColumn = "id", entityColumn = "checklistId")
    val items: List<ChecklistItemEntity>,
)
