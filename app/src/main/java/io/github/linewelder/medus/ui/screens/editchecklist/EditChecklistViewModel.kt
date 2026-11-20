package io.github.linewelder.medus.ui.screens.editchecklist

import androidx.lifecycle.*
import androidx.navigation.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import javax.inject.*

@HiltViewModel
class EditChecklistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ChecklistRepository,
) : ViewModel() {
    private val checklistId = Id.fromString<Checklist>(
        savedStateHandle.toRoute<EditChecklistRoute>().checklistId,
    )
    private val pendingWrites = Mutex()

    val checklist = repository.findById(checklistId)

    fun addItem(title: String) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isNotEmpty()) save { repository.addItem(checklistId, trimmedTitle) }
    }

    fun renameItem(id: Id<ChecklistItem>, title: String) = save { repository.updateItem(id, title) }

    fun deleteItem(id: Id<ChecklistItem>) = save { repository.removeItem(id) }

    fun moveItem(id: Id<ChecklistItem>, newIndex: Int) =
        save { repository.moveItem(checklistId, id, newIndex) }

    /** Serialize edits and finish them even if the user leaves the screen. */
    private fun save(write: suspend () -> Unit) {
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            withContext(NonCancellable) {
                pendingWrites.withLock { write() }
            }
        }
    }
}
