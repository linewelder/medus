package io.github.linewelder.medus.ui.screens.edittask

import android.database.sqlite.*
import androidx.lifecycle.*
import androidx.navigation.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import kotlin.time.*

/** Load the task identified by the route and persist edits or deletion. */
@HiltViewModel
class EditTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val areaRepository: AreaRepository,
) : ViewModel() {
    private val taskId = Id.fromString<Task>(savedStateHandle.toRoute<EditTaskRoute>().taskId)
    val areas = areaRepository.findAll()

    private val mutableSelectedAreaId = savedStateHandle.getMutableStateFlow<String?>("selectedAreaId", null)
    val selectedAreaId = mutableSelectedAreaId.asStateFlow()

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

    fun selectArea(area: Area) {
        mutableSelectedAreaId.value = area.id.toString()
    }

    /** Insert a new area and select it after the insert succeeds. */
    fun createArea(name: String) {
        val areaName = name.trim()
        if (areaName.isEmpty()) return

        applyChange(failure = ChangeState.AreaCreationFailed) {
            val area = Area(id = Id.random(), name = areaName)
            areaRepository.insert(area)
            selectArea(area)
            ChangeState.Editing
        }
    }

    fun saveTask(
        title: String,
        areaId: Id<Area>?,
        completed: Boolean,
        timeEstimateMins: Int?,
        priceEstimate: Int?,
    ) {
        val taskTitle = title.trim()
        if (taskTitle.isEmpty()) return

        val loadedTask = (taskState.value as? TaskState.Loaded)?.task ?: return
        applyChange(failure = ChangeState.SaveFailed) {
            taskRepository.update(
                loadedTask
                    .withCompletion(completed, Clock.System.now())
                    .copy(
                        title = taskTitle,
                        areaId = areaId,
                        timeEstimateMins = timeEstimateMins,
                        priceEstimate = priceEstimate,
                    ),
            )
            ChangeState.Finished
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
        Editing, Working, Finished, SaveFailed, DeleteFailed, AreaCreationFailed;

        val blocksEditing get() = this == Working || this == Finished
    }
}
