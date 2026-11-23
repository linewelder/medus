package io.github.linewelder.medus.ui.components.activechecklist

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.*
import javax.inject.*
import kotlin.time.Clock

@HiltViewModel
internal class ActiveChecklistViewModel @Inject constructor(
    private val repository: ChecklistRepository,
) : ViewModel() {
    fun setItemCompleted(id: Id<ChecklistItem>, completed: Boolean) {
        viewModelScope.launch {
            repository.setItemCompleted(id, if (completed) {
                Clock.System.now()
            } else {
                null
            })
        }
    }
}
