package io.github.linewelder.medus.core.database.converters

import androidx.room.*
import kotlin.time.*

class InstantConverter {
    @TypeConverter
    fun persist(instant: Instant): Long = instant.toEpochMilliseconds()

    @TypeConverter
    fun parse(timestamp: Long): Instant = Instant.fromEpochMilliseconds(timestamp)
}
