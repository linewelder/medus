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
)
