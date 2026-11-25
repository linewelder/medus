package io.github.linewelder.medus.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import io.github.linewelder.medus.ui.navigation.*
import io.github.linewelder.medus.ui.theme.*
import io.github.linewelder.medus.util.*
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.Instant

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
        today = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        startOfToday = startOfToday,
        activeChecklistContent = activeChecklistContent,
        calendarContent = { date, modifier -> CalendarEventList(date, modifier) },
    )
}

/**
 * @param activeChecklistContent Is only composed if [activeChecklist] is not null
 */
@Composable
private fun HomeScreenContent(
    onNewTask: () -> Unit,
    activeChecklist: Checklist?,
    today: LocalDate,
    startOfToday: Instant,
    activeChecklistContent: (@Composable (Modifier) -> Unit),
    calendarContent: @Composable (date: LocalDate, modifier: Modifier) -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .consumeWindowInsets(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CurrentTimeLabel(Modifier.fillMaxWidth())

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

            Column {
                Text(stringResource(R.string.home_today_events), style = MaterialTheme.typography.titleLarge)
                calendarContent(today, Modifier.fillMaxWidth())
            }

            Column {
                Text(
                    stringResource(R.string.home_tomorrow_events),
                    style = MaterialTheme.typography.titleLarge
                )

                calendarContent(today.plus(1, DateTimeUnit.DAY), Modifier.fillMaxWidth())
            }

            Spacer(Modifier.weight(1f))

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
    val today = LocalDate(2026, 1, 1)
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

    var checklist by remember { mutableStateOf(Checklist(
        id = Id.empty(),
        dateCreated = Instant.fromEpochMilliseconds(0L),
        role = ChecklistRole.Morning,
        items = listOf(
            ChecklistItem(
                id = Id.dummy(1),
                title = "Brush teeth",
                dateCompleted = Instant.fromEpochMilliseconds(0L),
            ),
            ChecklistItem(
                id = Id.dummy(2),
                title = "Make bed",
                dateCompleted = Instant.fromEpochMilliseconds(110L),
            ),
            ChecklistItem(
                id = Id.dummy(3),
                title = "Drink water",
                dateCompleted = null,
            ),
        )
    )) }

    val completionReset = Instant.fromEpochMilliseconds(100L)

    MedusTheme(dynamicColor = false) {
        FullScreenPreview(selectedTab = BottomBarTab.Home) {
            HomeScreenContent(
                onNewTask = { },
                activeChecklist = checklist,
                startOfToday = completionReset,
                today = today,
                activeChecklistContent = { modifier ->
                    ActiveChecklistContent(
                        checklist = checklist,
                        completionReset = completionReset,
                        onCheckedChange = { id, checked ->
                            checklist = checklist.copy(items = checklist.items.map { item ->
                                if (item.id == id) {
                                    item.copy(dateCompleted = if (checked) Clock.System.now() else null)
                                } else item
                            })
                        },
                        modifier = modifier,
                    )
                },
                calendarContent = { date, modifier ->
                    CalendarEventListContent(
                        events = if (date == today) {
                            eventsToday
                        } else {
                            eventsTomorrow
                        },
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
