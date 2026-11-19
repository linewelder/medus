package io.github.linewelder.medus.ui.components.calendar

import android.*
import android.content.pm.*
import android.text.format.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.core.content.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.ui.theme.*

/** Connect the calendar list to runtime permissions and live calendar state. */
@Composable
internal fun DeviceCalendarEventList(
    modifier: Modifier = Modifier,
    viewModel: CalendarEventsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val permission = Manifest.permission.READ_CALENDAR
    var hasCalendarAccess by remember(context) {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val calendarState by viewModel.state.collectAsStateWithLifecycle()
    val requestCalendarAccess = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCalendarAccess = granted
        viewModel.refresh(granted)
    }

    LifecycleResumeEffect(context) {
        hasCalendarAccess = ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
        viewModel.refresh(hasCalendarAccess)
        onPauseOrDispose {}
    }

    CalendarEventList(
        events = calendarState.events,
        hasCalendarAccess = hasCalendarAccess,
        loading = calendarState.loading,
        onRequestCalendarAccess = { requestCalendarAccess.launch(permission) },
        modifier = modifier,
    )
}

/** Render the calendar list for the supplied permission, loading, and event data. */
@Composable
internal fun CalendarEventList(
    events: CalendarEventDays?,
    hasCalendarAccess: Boolean,
    loading: Boolean,
    onRequestCalendarAccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        !hasCalendarAccess -> Column {
            Text(stringResource(R.string.home_calendar_permission))
            Button(onClick = onRequestCalendarAccess) {
                Text(stringResource(R.string.home_allow_calendar_access))
            }
        }

        loading && events == null -> CircularProgressIndicator()

        events == null -> Text(stringResource(R.string.home_calendar_unavailable))

        else -> LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(stringResource(R.string.home_today_events), style = MaterialTheme.typography.titleLarge)
            }
            calendarEventRows(events.today, R.string.home_no_events)

            item {
                Text(
                    stringResource(R.string.home_tomorrow_events),
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            calendarEventRows(events.tomorrow, R.string.home_no_tomorrow_events)
        }
    }
}

/** Add the day's event rows or its empty message to the shared list. */
private fun LazyListScope.calendarEventRows(events: List<CalendarEvent>, emptyMessage: Int) {
    if (events.isEmpty()) {
        item { Text(stringResource(emptyMessage)) }
    } else {
        items(events) { event ->
            val timeFrame = if (event.allDay) {
                stringResource(R.string.home_all_day)
            } else {
                DateUtils.formatDateRange(LocalContext.current, event.start, event.end, DateUtils.FORMAT_SHOW_TIME)
            }
            Column {
                Text(event.title ?: stringResource(R.string.home_untitled_event))
                Text(timeFrame, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview
@Composable
private fun CalendarEventListPreview() {
    val events = CalendarEventDays(
        today = listOf(
            CalendarEvent("School holiday", start = 0L, end = 86_400_000L, allDay = true),
            CalendarEvent("Pick up groceries", start = 36_000_000L, end = 39_600_000L, allDay = false),
        ),
        tomorrow = listOf(
            CalendarEvent("Dentist appointment", start = 122_400_000L, end = 124_200_000L, allDay = false),
        ),
    )

    MedusTheme(dynamicColor = false) {
        CalendarEventList(
            events = events,
            hasCalendarAccess = true,
            loading = false,
            onRequestCalendarAccess = {},
        )
    }
}
