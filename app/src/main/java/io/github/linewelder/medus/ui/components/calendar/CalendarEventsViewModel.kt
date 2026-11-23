package io.github.linewelder.medus.ui.components.calendar

import android.content.*
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import dagger.hilt.android.qualifiers.*
import io.github.linewelder.medus.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import javax.inject.*

/** The current result of reading the upcoming calendar events. */
internal data class CalendarEventsState(
    val loading: Boolean = false,
    val events: List<CalendarEvent>? = null,
)

/** Load upcoming calendar events off the main thread and expose the result to the UI. */
@HiltViewModel
internal class CalendarEventsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ViewModel() {
    private val calendarState = MutableStateFlow(CalendarEventsState())
    private var refreshJob: Job? = null

    val state = calendarState.asStateFlow()

    /** Load the device calendar events for the requested day. */
    fun loadEvents(date: LocalDate) {
        refreshJob?.cancel()
        refreshJob = null

        calendarState.update { it.copy(loading = true) }

        refreshJob = viewModelScope.launch {
            val events = withContext(Dispatchers.IO) {
                readCalendarEvents(context.contentResolver, date)
            }
            calendarState.value = CalendarEventsState(
                loading = false,
                events = events,
            )
        }
    }
}
