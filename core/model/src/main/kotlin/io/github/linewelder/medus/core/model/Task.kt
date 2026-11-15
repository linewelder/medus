package io.github.linewelder.medus.core.model

import java.util.*

data class Task(
    val id: Id<Task>,
    val title: String,
    val dateCreated: Date,
    val completed: Boolean,
    val areaId: Id<Area>? = null,
)
