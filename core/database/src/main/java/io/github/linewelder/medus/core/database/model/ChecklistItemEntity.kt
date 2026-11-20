package io.github.linewelder.medus.core.database.model

import androidx.room.*
import io.github.linewelder.medus.core.model.*
import kotlin.time.*

@Entity(
    tableName = "checklist_items",
    indices = [Index(value = ["checklistId"])],
    foreignKeys = [
        ForeignKey(
            entity = ChecklistEntity::class,
            parentColumns = ["id"],
            childColumns = ["checklistId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
class ChecklistItemEntity(
    @PrimaryKey
    val id: Id<ChecklistItem>,
    val title: String,
    val dateCompleted: Instant?,
    val checklistId: Id<Checklist>,
    val position: Int,
)
