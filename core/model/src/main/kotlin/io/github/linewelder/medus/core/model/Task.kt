package io.github.linewelder.medus.core.model

import kotlin.time.*

data class Task(
    val id: Id<Task>,
    val title: String,
    val dateCreated: Instant,
    val dateCompleted: Instant?,
    val areaId: Id<Area>? = null,
    val timeEstimateMins: Int? = null,
    val priceEstimate: Int? = null,
) {
    val completed = dateCompleted != null

    /** Mark the task complete or reopen it, preserving an existing completion timestamp. */
    fun withCompletion(newCompleted: Boolean, now: Instant): Task =
        if (completed != newCompleted) {
            copy(dateCompleted = if (completed) dateCompleted ?: now else null)
        } else {
            this
        }
}
