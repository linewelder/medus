package io.github.linewelder.medus.ui.screens.home

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import javax.inject.*
import kotlin.time.Clock

@HiltViewModel
internal class HomeViewModel @Inject constructor(repository: ChecklistRepository) : ViewModel() {
    val morningChecklist = repository.findByRole(ChecklistRole.Morning)

    val startOfToday = flow {
        while (true) {
            val zone = TimeZone.currentSystemDefault()
            val today = Clock.System.now().toLocalDateTime(zone).date
            emit(today.atStartOfDayIn(zone))

            val tomorrowStart = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(zone)
            delay(tomorrowStart - Clock.System.now())
        }
    }
}
