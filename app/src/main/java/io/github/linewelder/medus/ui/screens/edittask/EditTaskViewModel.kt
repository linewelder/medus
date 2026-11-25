package io.github.linewelder.medus.ui.screens.edittask

import android.database.sqlite.SQLiteException
import androidx.lifecycle.*
import androidx.navigation.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.EditTaskRoute
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

/** Load the task identified by the route and persist edits or deletion. */
@HiltViewModel
class EditTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val areaRepository: AreaRepository,
) : ViewModel() {
    private val taskId = Id.fromString<Task>(savedStateHandle.toRoute<EditTaskRoute>().taskId)
    val areas = areaRepository.findAll()

    private val mutableTaskState = MutableStateFlow<TaskState>(TaskState.Loading)
    val taskState = mutableTaskState.asStateFlow()

    private val mutableChangeState = MutableStateFlow(ChangeState.Editing)
    val changeState = mutableChangeState.asStateFlow()

    init {
        viewModelScope.launch {
            mutableTaskState.value = try {
                taskRepository.findById(taskId)?.let(TaskState::Loaded) ?: TaskState.Unavailable
            } catch (_: SQLiteException) {
                TaskState.Unavailable
            }
        }
    }

    /** Save the edited fields and create an area if a new name was entered. */
    fun saveTask(
        title: String,
        areaId: Id<Area>?,
        completed: Boolean,
        newAreaName: String?,
        timeEstimateMins: Int?,
        priceEstimate: Int?,
    ) {
        val taskTitle = title.trim()
        val areaName = newAreaName?.trim()
        if (taskTitle.isEmpty() || areaName == "") return

        applyChange(failure = ChangeState.SaveFailed) {
            val updated = taskRepository.update(
                taskId, taskTitle, areaId, completed, areaName, timeEstimateMins, priceEstimate,
            )
            if (updated) {
                ChangeState.Finished
            } else {
                mutableTaskState.value = TaskState.Unavailable
                ChangeState.Editing
            }
        }
    }

    fun deleteTask() {
        applyChange(failure = ChangeState.DeleteFailed) {
            taskRepository.delete(taskId)
            ChangeState.Finished
        }
    }

    /** Run one database change at a time and leave failed changes available for retry. */
    private fun applyChange(failure: ChangeState, change: suspend () -> ChangeState) {
        val canEdit = taskState.value is TaskState.Loaded && !changeState.value.blocksEditing
        if (!canEdit) return

        mutableChangeState.value = ChangeState.Working
        viewModelScope.launch {
            mutableChangeState.value = try {
                change()
            } catch (_: SQLiteException) {
                failure
            }
        }
    }

    sealed interface TaskState {
        data object Loading : TaskState
        data class Loaded(val task: Task) : TaskState
        data object Unavailable : TaskState
    }

    enum class ChangeState {
        Editing, Working, Finished, SaveFailed, DeleteFailed;

        val blocksEditing get() = this == Working || this == Finished
    }
}
