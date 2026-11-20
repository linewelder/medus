package io.github.linewelder.medus.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.ui.components.*
import io.github.linewelder.medus.ui.components.calendar.*
import io.github.linewelder.medus.ui.theme.*
import io.github.linewelder.medus.util.*
import kotlin.time.*

@Composable
fun HomeScreen(
    onNewTask: () -> Unit,
) {
    HomeScreenContent(
        onNewTask = onNewTask,
    ) { modifier ->
        DeviceCalendarEventList(modifier)
    }
}

/** Apply the screen background and system insets to the home content. */
@Composable
private fun HomeScreenContent(
    onNewTask: () -> Unit,
    calendarContent: @Composable (Modifier) -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .consumeWindowInsets(innerPadding),
        ) {
            CurrentTimeLabel(Modifier.fillMaxWidth().padding(bottom = 8.dp))

            calendarContent(Modifier.weight(1f).fillMaxWidth())

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNewTask,
            ) {
                Text(stringResource(R.string.new_task))
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    val eventsToday = listOf(
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
    )
    val eventsTomorrow = listOf(
        CalendarEvent(
            title = "Dentist appointment",
            start = Instant.fromEpochMilliseconds(122_400_000L),
            end = Instant.fromEpochMilliseconds(124_200_000L),
            allDay = false,
        ),
    )

    MedusTheme(dynamicColor = false) {
        HomeScreenContent(
            onNewTask = { },
        ) { modifier ->
            CalendarEventList(
                eventsToday = eventsToday,
                eventsTomorrow = eventsTomorrow,
                hasCalendarAccess = true,
                loading = false,
                onRequestCalendarAccess = {},
                modifier = modifier,
            )
        }
    }
}
