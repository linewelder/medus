package io.github.linewelder.medus.core.database

import androidx.room.*
import io.github.linewelder.medus.core.database.converters.*
import io.github.linewelder.medus.core.database.model.*

@Database(entities = [TaskEntity::class], version = 1)
@TypeConverters(
    DateConverter::class,
)
abstract class MedusDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
