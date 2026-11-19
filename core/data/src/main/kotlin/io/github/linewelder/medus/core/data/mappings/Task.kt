package io.github.linewelder.medus.core.data.mappings

import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*

fun TaskEntity.asExternalModel() =
    Task(
        id = id.unsafeCast(),
        title = title,
        dateCreated = dateCreated,
        dateCompleted = dateCompleted,
        areaId = areaId,
        timeEstimateMins = timeEstimateMins,
    )
