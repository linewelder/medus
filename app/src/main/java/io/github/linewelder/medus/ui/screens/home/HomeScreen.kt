package io.github.linewelder.medus.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.components.*
import io.github.linewelder.medus.ui.components.activechecklist.*
import io.github.linewelder.medus.ui.components.calendar.*
import io.github.linewelder.medus.ui.theme.*
import io.github.linewelder.medus.util.*
import kotlin.time.*

@Composable
fun HomeScreen(
    onNewTask: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val checklist by viewModel.morningChecklist.collectAsStateWithLifecycle(initialValue = null)
    val startOfToday by viewModel.startOfToday
        .collectAsStateWithLifecycle(initialValue = Instant.fromEpochMilliseconds(0))

    val activeChecklistContent = @Composable checklist@{ modifier: Modifier ->
        ActiveChecklist(
            modifier = modifier,
            checklist = checklist!!,
            completionReset = startOfToday,
        )
    }

    HomeScreenContent(
        onNewTask = onNewTask,
        activeChecklist = checklist,
        startOfToday = startOfToday,
        activeChecklistContent = activeChecklistContent,
        calendarContent = { modifier -> DeviceCalendarEventList(modifier) },
    )
}

/**
 * @param activeChecklistContent Is only composed if [activeChecklist] is not null
 */
@Composable
private fun HomeScreenContent(
    onNewTask: () -> Unit,
    activeChecklist: Checklist?,
    startOfToday: Instant,
    activeChecklistContent: (@Composable (Modifier) -> Unit),
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

            if (activeChecklist != null) {
                fun <T> animationSpec() = tween<T>(
                    durationMillis = 500,
                    delayMillis = 200,
                )

                AnimatedVisibility(
                    visible = !activeChecklist.isCompleted(startOfToday),
                    exit = fadeOut(animationSpec()) + shrinkVertically(animationSpec()),
                ) {
                    activeChecklistContent(Modifier.fillMaxWidth())
                }
            }

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

    val checklist = Checklist(
        id = Id.empty(),
        dateCreated = Instant.fromEpochMilliseconds(0L),
        role = ChecklistRole.Morning,
        items = listOf(
            ChecklistItem(
                id = Id.empty(),
                title = "Brush teeth",
                dateCompleted = Instant.fromEpochMilliseconds(0L),
            ),
            ChecklistItem(
                id = Id.empty(),
                title = "Make bed",
                dateCompleted = Instant.fromEpochMilliseconds(110L),
            ),
            ChecklistItem(
                id = Id.empty(),
                title = "Drink water",
                dateCompleted = null,
            ),
        )
    )

    val completionReset = Instant.fromEpochMilliseconds(100L)

    MedusTheme(dynamicColor = false) {
        ConfettiOverlay {
            HomeScreenContent(
                onNewTask = { },
                activeChecklist = checklist,
                startOfToday = completionReset,
                activeChecklistContent = { modifier ->
                    ActiveChecklistContent(
                        checklist = checklist,
                        completionReset = completionReset,
                        onCheckedChange = { _, _ -> },
                        modifier = modifier,
                    )
                },
                calendarContent = { modifier ->
                    CalendarEventList(
                        eventsToday = eventsToday,
                        eventsTomorrow = eventsTomorrow,
                        hasCalendarAccess = true,
                        loading = false,
                        onRequestCalendarAccess = {},
                        modifier = modifier,
                    )
                },
            )
        }
    }
}
