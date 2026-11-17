package io.github.linewelder.medus.ui.screens.menu

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import kotlinx.coroutines.*
import javax.inject.*

/** Expose the saved areas and persist changes to their order. */
@HiltViewModel
class MenuViewModel @Inject constructor(
    private val areaRepository: AreaRepository,
) : ViewModel() {
    val areas = areaRepository.findAllAreas()

    /** Save the area's new position relative to its current position. */
    fun moveArea(id: Id<Area>, newIndex: Int) {
        viewModelScope.launch {
            areaRepository.moveArea(id, newIndex)
        }
    }
}
