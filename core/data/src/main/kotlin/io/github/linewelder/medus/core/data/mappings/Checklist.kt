package io.github.linewelder.medus.core.data.mappings

import io.github.linewelder.medus.core.database.dao.*
import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*

internal fun ChecklistWithItems.asExternalModel() = Checklist(
    id = checklist.id,
    dateCreated = checklist.dateCreated,
    role = checklist.role,
    items = items
        .sortedWith(compareBy({ it.position }))
        .map(ChecklistItemEntity::asExternalModel),
)

internal fun ChecklistItemEntity.asExternalModel() = ChecklistItem(
    id = id,
    title = title,
    dateCompleted = dateCompleted,
)
