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
import io.github.linewelder.medus.util.*
import kotlinx.datetime.*
import kotlin.time.Instant

/** Connect the calendar list to runtime permissions and live calendar state. */
@Composable
internal fun CalendarEventList(
    date: LocalDate,
    modifier: Modifier = Modifier,
    viewModel: CalendarEventsViewModel = hiltViewModel(key = "CalendarEvents:$date"),
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
    }

    LifecycleResumeEffect(context) {
        hasCalendarAccess = ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
        onPauseOrDispose {}
    }

    LaunchedEffect(hasCalendarAccess, date, viewModel) {
        if (hasCalendarAccess) {
            viewModel.loadEvents(date)
        }
    }

    CalendarEventListContent(
        events = calendarState.events,
        hasCalendarAccess = hasCalendarAccess,
        loading = calendarState.loading,
        onRequestCalendarAccess = { requestCalendarAccess.launch(permission) },
        modifier = modifier,
    )
}

/** Render the calendar list for the supplied permission, loading, and event data. */
@Composable
internal fun CalendarEventListContent(
    events: List<CalendarEvent>?,
    hasCalendarAccess: Boolean,
    loading: Boolean,
    onRequestCalendarAccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        !hasCalendarAccess -> Column {
            Text(stringResource(R.string.calendar_permission))
            Button(onClick = onRequestCalendarAccess) {
                Text(stringResource(R.string.allow_calendar_access))
            }
        }

        events == null ->
            if (loading) {
                CircularProgressIndicator()
            } else {
                Text(stringResource(R.string.calendar_unavailable))
            }

        else -> LazyColumn(modifier = modifier) {
            if (events.isEmpty()) {
                item { Text(stringResource(R.string.no_events)) }
            } else {
                items(events) { CalendarEventDisplay(it) }
            }
        }
    }
}

@Composable
private fun CalendarEventDisplay(event: CalendarEvent) {
    val timeFrame = if (event.allDay) {
        stringResource(R.string.event_all_day)
    } else {
        DateUtils.formatDateRange(
            LocalContext.current,
            event.start.toEpochMilliseconds(),
            event.end.toEpochMilliseconds(),
            DateUtils.FORMAT_SHOW_TIME,
        )
    }

    Column(Modifier.padding(vertical = 8.dp)) {
        Text(event.title ?: stringResource(R.string.untitled_event))
        Text(timeFrame, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarEventListContentPreview() {
    MedusTheme(dynamicColor = false) {
        CalendarEventListContent(
            events = listOf(
                CalendarEvent(
                    title = "School holiday",
                    start = Instant.fromEpochMilliseconds(0L),
                    end = Instant.fromEpochMilliseconds(86_400_000L),
                    allDay = true,
                ),
                CalendarEvent(
                    title = "Pick up groceries",
                    start = Instant.fromEpochMilliseconds(36_000_000L),
                    end = Instant.fromEpochMilliseconds(39_600_000L),
                    allDay = false,
                ),
            ),
            hasCalendarAccess = true,
            loading = false,
            onRequestCalendarAccess = {},
        )
    }
}
