package io.github.linewelder.medus.ui.screens.alltasks

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.*
import io.github.linewelder.medus.ui.actions.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AllTasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val areaRepository: AreaRepository,
    private val actionHistory: ActionHistory,
) : ViewModel() {
    val tasks = taskRepository.findAll()
    val areas = areaRepository.findAllWithTaskCounts()

    fun setTaskCompleted(task: Task, completed: Boolean) {
        viewModelScope.launch {
            taskRepository.setCompleted(task.id, completed)
            if (completed) {
                actionHistory.commit(TaskCompletedAction(taskRepository, task))
            }
        }
    }
}
