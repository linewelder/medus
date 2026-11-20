package io.github.linewelder.medus.ui.components.calendar

import android.content.*
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import dagger.hilt.android.qualifiers.*
import io.github.linewelder.medus.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.*

/** The current result of reading the upcoming calendar events. */
internal data class CalendarEventsState(
    val loading: Boolean = false,
    val eventsToday: List<CalendarEvent>? = null,
    val eventsTomorrow: List<CalendarEvent>? = null,
)

/** Load upcoming calendar events off the main thread and expose the result to the UI. */
@HiltViewModel
internal class CalendarEventsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ViewModel() {
    private val calendarState = MutableStateFlow(CalendarEventsState())
    private var refreshJob: Job? = null

    val state = calendarState.asStateFlow()

    /** Refresh calendar event list. */
    fun refresh(hasCalendarAccess: Boolean) {
        if (!hasCalendarAccess) return

        refreshJob?.cancel()
        refreshJob = null

        calendarState.update { it.copy(loading = true) }

        refreshJob = viewModelScope.launch {
            val eventsToday = withContext(Dispatchers.IO) {
                val today = Calendar.getInstance()
                readCalendarEvents(context.contentResolver, today)
            }
            val eventsTomorrow = withContext(Dispatchers.IO) {
                val tomorrow = Calendar.getInstance()
                tomorrow.add(Calendar.DAY_OF_MONTH, 1)
                readCalendarEvents(context.contentResolver, tomorrow)
            }
            calendarState.value = CalendarEventsState(
                loading = false,
                eventsToday = eventsToday,
                eventsTomorrow = eventsTomorrow,
            )
        }
    }
}
