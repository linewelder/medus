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

/** Read visible event occurrences that overlap today on the device. */
internal fun readTodayCalendarEvents(resolver: ContentResolver): List<CalendarEvent>? {
    val today = Calendar.getInstance()
    val year = today.get(Calendar.YEAR)
    val month = today.get(Calendar.MONTH)
    val day = today.get(Calendar.DAY_OF_MONTH)

    val localDay = Calendar.getInstance(today.timeZone)
    localDay.clear()
    localDay.set(year, month, day)
    val localStart = localDay.timeInMillis
    localDay.clear()
    localDay.set(year, month, day + 1)
    val localEnd = localDay.timeInMillis

    val utcDay = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utcDay.clear()
    utcDay.set(year, month, day)
    val allDayStart = utcDay.timeInMillis
    utcDay.add(Calendar.DAY_OF_MONTH, 1)
    val allDayEnd = utcDay.timeInMillis

    val projection = arrayOf(Events.TITLE, Instances.BEGIN, Instances.END, Events.ALL_DAY)
    val occurrences = try {
        Instances.query(
            resolver,
            projection,
            minOf(localStart, allDayStart),
            maxOf(localEnd, allDayEnd),
        )
    } catch (_: SecurityException) {
        return null
    }

    return occurrences?.use { cursor ->
        buildList {
            while (cursor.moveToNext()) {
                val start = cursor.getLong(1)
                val end = cursor.getLong(2)
                val allDay = cursor.getInt(3) != 0
                val dayStart = if (allDay) allDayStart else localStart
                val dayEnd = if (allDay) allDayEnd else localEnd
                val startsToday = start in dayStart until dayEnd
                val carriesIntoToday = start < dayStart && end > dayStart
                if (startsToday || carriesIntoToday) {
                    val title = cursor.getString(0)?.takeUnless { it.isBlank() }
                    add(CalendarEvent(title, start, end, allDay))
                }
            }
        }.sortedWith(compareByDescending<CalendarEvent> { it.allDay }.thenBy { it.start })
    }
}
