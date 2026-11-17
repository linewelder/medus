package io.github.linewelder.medus.ui.screens.createtask

import android.database.sqlite.SQLiteException
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

/** Load the available areas and track the task save for the lifetime of the screen. */
@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val areaRepository: AreaRepository,
) : ViewModel() {
    val areas = areaRepository.findAllAreas()

    private val mutableSaveState = MutableStateFlow(SaveState.Editing)
    val saveState = mutableSaveState.asStateFlow()

    /** Save the task with the selected area or create an area from the entered name. */
    fun saveTask(title: String, selectedArea: Area?, newAreaName: String) {
        val canSave = saveState.value == SaveState.Editing || saveState.value == SaveState.Failed
        val taskTitle = title.trim()
        val areaName = newAreaName.trim()
        val hasArea = selectedArea != null || areaName.isNotEmpty()
        if (!canSave || taskTitle.isEmpty() || !hasArea) return

        mutableSaveState.value = SaveState.Saving
        viewModelScope.launch {
            val area = selectedArea ?: Area(id = Id.random(), name = areaName)
            try {
                taskRepository.createTask(taskTitle, area, isNewArea = selectedArea == null)
                mutableSaveState.value = SaveState.Saved
            } catch (_: SQLiteException) {
                mutableSaveState.value = SaveState.Failed
            }
        }
    }

    enum class SaveState { Editing, Saving, Saved, Failed }
}
