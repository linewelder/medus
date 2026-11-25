package io.github.linewelder.medus.core.data.mappings

import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*

fun TaskEntity.asExternalModel() =
    Task(
        id = id,
        title = title,
        dateCreated = dateCreated,
        dateCompleted = dateCompleted,
        areaId = areaId,
        timeEstimateMins = timeEstimateMins,
        priceEstimate = priceEstimate,
    )

fun Task.asEntity() = TaskEntity(
    id = id,
    title = title,
    dateCreated = dateCreated,
    dateCompleted = dateCompleted,
    areaId = areaId,
    timeEstimateMins = timeEstimateMins,
    priceEstimate = priceEstimate,
)
