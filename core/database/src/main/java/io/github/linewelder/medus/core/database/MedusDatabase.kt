package io.github.linewelder.medus.core.database

import androidx.room.*
import io.github.linewelder.medus.core.database.converters.*
import io.github.linewelder.medus.core.database.model.*

@Database(
    entities = [
        TaskEntity::class,
        AreaEntity::class,
    ],
    version = 5,
)
@TypeConverters(
    InstantConverter::class,
)
abstract class MedusDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun areaDao(): AreaDao
}
