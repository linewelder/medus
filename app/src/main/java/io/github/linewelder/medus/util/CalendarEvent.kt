package io.github.linewelder.medus.util

import android.content.*
import android.provider.CalendarContract.*
import kotlinx.datetime.*
import kotlin.time.Instant

data class CalendarEvent(
    val title: String?,
    val start: Instant,
    val end: Instant,
    val allDay: Boolean,
)

/** Read device calendar events for the requested day. */
fun readCalendarEvents(resolver: ContentResolver, date: LocalDate): List<CalendarEvent>? {
    val nextDate = date.plus(1, DateTimeUnit.DAY)

    fun dayBoundaries(timeZone: TimeZone) = listOf(
        date.atStartOfDayIn(timeZone).toEpochMilliseconds(),
        nextDate.atStartOfDayIn(timeZone).toEpochMilliseconds() - 1,
    )

    val localBoundaries = dayBoundaries(TimeZone.currentSystemDefault())
    val allDayBoundaries = dayBoundaries(TimeZone.UTC)

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
