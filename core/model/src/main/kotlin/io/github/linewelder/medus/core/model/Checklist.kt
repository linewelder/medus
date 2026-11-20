package io.github.linewelder.medus.core.model

import kotlin.time.*

data class Checklist(
    val id: Id<Checklist>,
    val dateCreated: Instant,
    val role: ChecklistRole?,
    val items: List<ChecklistItem>,
)

data class ChecklistItem(
    val id: Id<ChecklistItem>,
    val title: String,
    val dateCompleted: Instant? = null,
)

enum class ChecklistRole { Morning, Evening }
