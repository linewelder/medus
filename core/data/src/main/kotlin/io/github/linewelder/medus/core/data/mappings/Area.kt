package io.github.linewelder.medus.core.data.mappings

import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*

fun AreaEntity.asExternalModel(): Area =
    Area(
        id = id,
        name = name,
        sortOrder = sortOrder,
    )

fun Area.asEntity() =
    AreaEntity(
        id = id,
        name = name,
        sortOrder = sortOrder
    )
