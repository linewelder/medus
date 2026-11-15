package io.github.linewelder.medus.ui.home

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import javax.inject.*

@HiltViewModel
class HomeViewModel @Inject constructor(taskRepository: TaskRepository) : ViewModel() {
    val tasks = taskRepository.findAllTasks()
}
