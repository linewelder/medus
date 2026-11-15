package io.github.linewelder.medus.core.database.converters

import androidx.room.*
import java.util.*

class DateConverter {
    @TypeConverter
    fun persist(date: Date): Long = date.time

    @TypeConverter
    fun parse(timestamp: Long): Date = Date(timestamp)
}
