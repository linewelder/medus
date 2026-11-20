package io.github.linewelder.medus.util

import android.content.*
import android.provider.CalendarContract.*
import kotlin.time.*
import java.util.*

data class CalendarEvent(
    val title: String?,
    val start: Instant,
    val end: Instant,
    val allDay: Boolean,
)

fun readCalendarEvents(resolver: ContentResolver, date: Calendar): List<CalendarEvent>? {
    val year = date.get(Calendar.YEAR)
    val month = date.get(Calendar.MONTH)
    val day = date.get(Calendar.DAY_OF_MONTH)

    /**
     * Get the start and the end of today's date in the given time zone
     * as milliseconds since the Unix Epoch.
     */
    fun dayBoundaries(timeZone: TimeZone): List<Long> {
        val calendar = Calendar.getInstance(timeZone)
        return List(2) { dayOffset ->
            calendar.clear()
            calendar.set(year, month, day + dayOffset)
            calendar.timeInMillis
        }
    }

    val localBoundaries = dayBoundaries(date.timeZone)
    val allDayBoundaries = dayBoundaries(TimeZone.getTimeZone("UTC"))

    val fields = arrayOf(Events.TITLE, Instances.BEGIN, Instances.END, Events.ALL_DAY)
    val occurrences = try {
        Instances.query(
            resolver,
            fields,
            minOf(localBoundaries.first(), allDayBoundaries.first()),
            maxOf(localBoundaries.last(), allDayBoundaries.last()),
        )
    } catch (_: SecurityException) {
        return null
    }

    return occurrences.use { cursor ->
        buildList {
            while (cursor.moveToNext()) {
                add(CalendarEvent(
                    title = cursor.getString(0)?.takeUnless { it.isBlank() },
                    start = Instant.fromEpochMilliseconds(cursor.getLong(1)),
                    end = Instant.fromEpochMilliseconds(cursor.getLong(2)),
                    allDay = cursor.getInt(3) != 0,
                ))
            }
        }.sortedWith(
            compareByDescending<CalendarEvent> { it.allDay }
                .thenBy { it.start }
        )
    }
}
