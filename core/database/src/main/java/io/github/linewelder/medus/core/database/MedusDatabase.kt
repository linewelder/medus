package io.github.linewelder.medus.core.database

import androidx.room.*
import io.github.linewelder.medus.core.database.converters.*
import io.github.linewelder.medus.core.database.dao.*
import io.github.linewelder.medus.core.database.model.*

@Database(
    entities = [
        TaskEntity::class,
        AreaEntity::class,
        ChecklistEntity::class,
        ChecklistItemEntity::class,
    ],
    version = 6,
)
@TypeConverters(
    InstantConverter::class,
)
abstract class MedusDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun areaDao(): AreaDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun checklistItemDao(): ChecklistItemDao
}
