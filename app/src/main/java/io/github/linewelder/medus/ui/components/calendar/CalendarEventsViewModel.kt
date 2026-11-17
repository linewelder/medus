package io.github.linewelder.medus.ui.components.calendar

import android.content.*
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import dagger.hilt.android.qualifiers.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

/** The current result of reading the upcoming calendar events. */
internal data class CalendarEventsState(
    val loading: Boolean = false,
    val events: CalendarEventDays? = null,
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
            val events = withContext(Dispatchers.IO) {
                readCalendarEventDays(context.contentResolver)
            }
            calendarState.value = CalendarEventsState(
                loading = false,
                events = events,
            )
        }
    }
}
