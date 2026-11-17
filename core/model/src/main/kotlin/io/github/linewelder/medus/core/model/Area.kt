package io.github.linewelder.medus.core.model

data class Area(
    val id: Id<Area>,
    val name: String,
    var sortOrder: Long = 0,
)
