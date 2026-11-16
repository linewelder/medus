package io.github.linewelder.medus.ui.alltasks

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AllTasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    val tasks = taskRepository.findAllTasks()
    val areas = taskRepository.findAllAreas()

    fun setTaskCompleted(id: Id<Task>, completed: Boolean) {
        viewModelScope.launch {
            taskRepository.setTaskCompleted(id, completed)
        }
    }
}
