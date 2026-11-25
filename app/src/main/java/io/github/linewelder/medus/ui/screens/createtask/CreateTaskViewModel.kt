package io.github.linewelder.medus.ui.screens.createtask

import android.database.sqlite.SQLiteException
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import kotlin.time.*

/** Load the available areas and track the task save for the lifetime of the screen. */
@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val areaRepository: AreaRepository,
) : ViewModel() {
    val areas = areaRepository.findAll()

    private val mutableSelectedAreaId = savedStateHandle.getMutableStateFlow<String?>("selectedAreaId", null)
    val selectedAreaId = mutableSelectedAreaId.asStateFlow()

    private val mutableSaveState = MutableStateFlow(SaveState.Editing)
    val saveState = mutableSaveState.asStateFlow()

    fun selectArea(area: Area) {
        mutableSelectedAreaId.value = area.id.toString()
    }

    /** Insert a new area and select it after the insert succeeds. */
    fun createArea(name: String) {
        val areaName = name.trim()
        if (areaName.isEmpty()) return

        applyChange(failure = SaveState.AreaCreationFailed) {
            val area = Area(id = Id.random(), name = areaName)
            areaRepository.insert(area)
            selectArea(area)
            SaveState.Editing
        }
    }

    fun saveTask(
        title: String,
        areaId: Id<Area>,
        timeEstimateMins: Int?,
        priceEstimate: Int?,
    ) {
        val taskTitle = title.trim()
        if (taskTitle.isEmpty()) return

        applyChange(failure = SaveState.Failed) {
            taskRepository.insert(
                Task(
                    id = Id.random(),
                    title = taskTitle,
                    dateCreated = Clock.System.now(),
                    dateCompleted = null,
                    areaId = areaId,
                    timeEstimateMins = timeEstimateMins,
                    priceEstimate = priceEstimate,
                ),
            )
            SaveState.Saved
        }
    }

    /** Run one write at a time and let the user retry failed writes. */
    private fun applyChange(failure: SaveState, change: suspend () -> SaveState) {
        if (saveState.value.blocksEditing) return

        mutableSaveState.value = SaveState.Saving
        viewModelScope.launch {
            mutableSaveState.value = try {
                change()
            } catch (_: SQLiteException) {
                failure
            }
        }
    }

    enum class SaveState {
        Editing, Saving, Saved, Failed, AreaCreationFailed;

        val blocksEditing get() = this == Saving || this == Saved
    }
}
