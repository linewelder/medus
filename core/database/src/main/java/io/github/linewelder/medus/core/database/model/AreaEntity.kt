package io.github.linewelder.medus.core.database.model

import androidx.room.*
import io.github.linewelder.medus.core.model.*

@Entity(tableName = "areas")
data class AreaEntity(
    @PrimaryKey
    val id: Id<Area>,
    val name: String,
    @ColumnInfo(defaultValue = "0")
    val sortOrder: Long,
)
