package io.github.linewelder.medus.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.linewelder.medus.core.model.Area
import io.github.linewelder.medus.core.model.Id

@Entity(tableName = "areas")
data class AreaEntity(
    @PrimaryKey
    val id: Id<Area>,
    val name: String,
)
