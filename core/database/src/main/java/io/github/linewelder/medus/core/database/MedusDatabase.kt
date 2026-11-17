package io.github.linewelder.medus.core.database

import androidx.room.*
import io.github.linewelder.medus.core.database.converters.*
import io.github.linewelder.medus.core.database.model.*

@Database(
    entities = [
        TaskEntity::class,
        AreaEntity::class,
    ],
    version = 4,
)
@TypeConverters(
    DateConverter::class,
)
abstract class MedusDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun areaDao(): AreaDao
}
