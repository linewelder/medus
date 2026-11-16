package io.github.linewelder.medus.ui.components.calendar

import android.content.*
import android.provider.CalendarContract.*
import java.util.*

/** An occurrence from the device calendar with start and end in epoch milliseconds. */
internal data class CalendarEvent(
    val title: String?,
    val start: Long,
    val end: Long,
    val allDay: Boolean,
)

/** Calendar occurrences grouped by the device dates they overlap. */
internal data class CalendarEventDays(
    val today: List<CalendarEvent>,
    val tomorrow: List<CalendarEvent>,
)

/** Read visible event occurrences that overlap today or tomorrow on the device. */
internal fun readCalendarEventDays(resolver: ContentResolver): CalendarEventDays? {
    val today = Calendar.getInstance()
    val year = today.get(Calendar.YEAR)
    val month = today.get(Calendar.MONTH)
    val day = today.get(Calendar.DAY_OF_MONTH)

    fun dayBoundaries(timeZone: TimeZone): List<Long> {
        val calendar = Calendar.getInstance(timeZone)
        return List(3) { dayOffset ->
            calendar.clear()
            calendar.set(year, month, day + dayOffset)
            calendar.timeInMillis
        }
    }

    val localBoundaries = dayBoundaries(today.timeZone)
    val allDayBoundaries = dayBoundaries(TimeZone.getTimeZone("UTC"))

    val projection = arrayOf(Events.TITLE, Instances.BEGIN, Instances.END, Events.ALL_DAY)
    val occurrences = try {
        Instances.query(
            resolver,
            projection,
            minOf(localBoundaries.first(), allDayBoundaries.first()),
            maxOf(localBoundaries.last(), allDayBoundaries.last()),
        )
    } catch (_: SecurityException) {
        return null
    }

    val events = occurrences?.use { cursor ->
        buildList {
            while (cursor.moveToNext()) {
                val title = cursor.getString(0)?.takeUnless { it.isBlank() }
                val start = cursor.getLong(1)
                val end = cursor.getLong(2)
                val allDay = cursor.getInt(3) != 0
                add(CalendarEvent(title, start, end, allDay))
            }
        }.sortedWith(compareByDescending<CalendarEvent> { it.allDay }.thenBy { it.start })
    } ?: return null

    fun eventsForDay(dayOffset: Int): List<CalendarEvent> = events.filter { event ->
        val boundaries = if (event.allDay) allDayBoundaries else localBoundaries
        val dayStart = boundaries[dayOffset]
        val dayEnd = boundaries[dayOffset + 1]
        val startsOnDay = event.start in dayStart until dayEnd
        val carriesIntoDay = event.start < dayStart && event.end > dayStart
        startsOnDay || carriesIntoDay
    }

    return CalendarEventDays(today = eventsForDay(0), tomorrow = eventsForDay(1))
}
