package io.github.linewelder.medus.ui.navigation

import kotlinx.serialization.*

@Serializable
data object HomeRoute

@Serializable
data object AllTasksRoute

@Serializable
data object MenuRoute

@Serializable
data class CreateTaskRoute(val areaId: String? = null)

@Serializable
data class EditTaskRoute(val taskId: String)

@Serializable
data class EditChecklistRoute(val checklistId: String)
