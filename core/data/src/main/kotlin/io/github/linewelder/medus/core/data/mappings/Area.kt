package io.github.linewelder.medus.core.data.mappings

import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*

fun AreaEntity.asExternalModel(): Area =
    Area(
        id = this.id,
        name = this.name,
        sortOrder = this.sortOrder,
    )
