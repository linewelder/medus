package io.github.linewelder.medus.core.database.model

import androidx.room.*
import io.github.linewelder.medus.core.model.*
import kotlin.time.*

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = AreaEntity::class,
            parentColumns = ["id"],
            childColumns = ["areaId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("areaId")],
)
data class TaskEntity(
    @PrimaryKey
    val id: Id<Task>,
    val title: String,
    val dateCreated: Instant,
    val dateCompleted: Instant?,
    val areaId: Id<Area>? = null,
    val timeEstimateMins: Int? = null,
    val priceEstimate: Int? = null,
)
