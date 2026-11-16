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
    val loading: Boolean = true,
    val events: CalendarEventDays? = null,
)

/** Load upcoming calendar events off the main thread and expose the result to the UI. */
@HiltViewModel
internal class CalendarEventsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ViewModel() {
    private val calendarState = MutableStateFlow(CalendarEventsState())
    val state = calendarState.asStateFlow()
    private var refreshJob: Job? = null

    /** Refresh events if access is granted and discard cached events if access is revoked. */
    fun refresh(hasCalendarAccess: Boolean) {
        refreshJob?.cancel()
        calendarState.value = CalendarEventsState(loading = hasCalendarAccess)
        refreshJob = if (hasCalendarAccess) {
            viewModelScope.launch {
                val events = withContext(Dispatchers.IO) {
                    readCalendarEventDays(context.contentResolver)
                }
                calendarState.value = CalendarEventsState(loading = false, events = events)
            }
        } else {
            null
        }
    }
}
