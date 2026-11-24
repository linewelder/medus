package io.github.linewelder.medus.ui.screens.menu

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.*
import javax.inject.*

/** Expose the saved areas and handle reordering and deletion. */
@HiltViewModel
class MenuViewModel @Inject constructor(
    private val areaRepository: AreaRepository,
    checklistRepository: ChecklistRepository,
) : ViewModel() {
    val checklists = checklistRepository.findAll()
    val areas = areaRepository.findAll()
    val emptyAreaIds = areaRepository.findEmptyIds()

    fun moveArea(id: Id<Area>, newIndex: Int) {
        viewModelScope.launch {
            areaRepository.reorder(id, newIndex)
        }
    }

    fun deleteEmptyArea(id: Id<Area>) {
        viewModelScope.launch {
            areaRepository.delete(id)
        }
    }
}
