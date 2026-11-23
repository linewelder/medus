package io.github.linewelder.medus.core.model

import kotlin.time.*

data class Checklist(
    val id: Id<Checklist>,
    val dateCreated: Instant,
    val role: ChecklistRole?,
    val items: List<ChecklistItem>,
) {
    /**
     * For checklists that reset every day, the `resetDate` is the start of the current day.
     */
    fun isCompleted(resetDate: Instant?) =
        items.all { it.isCompleted(resetDate) }
}

data class ChecklistItem(
    val id: Id<ChecklistItem>,
    val title: String,
    val dateCompleted: Instant? = null,
) {
    /**
     * For checklists that reset every day, the `resetDate` is the start of the current day.
     */
    fun isCompleted(resetDate: Instant?) =
        dateCompleted != null && (resetDate == null || dateCompleted > resetDate)
}

enum class ChecklistRole { Morning, Evening }
