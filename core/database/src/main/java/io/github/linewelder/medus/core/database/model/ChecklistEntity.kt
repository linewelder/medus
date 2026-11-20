package io.github.linewelder.medus.core.database.model

import androidx.room.*
import io.github.linewelder.medus.core.model.*
import kotlin.time.*

@Entity(
    tableName = "checklists",
    indices = [
        Index(value = ["role"], unique = true),
    ],
)
data class ChecklistEntity(
    @PrimaryKey
    val id: Id<Checklist>,
    val dateCreated: Instant,
    val role: ChecklistRole?,
)
