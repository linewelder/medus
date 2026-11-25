package io.github.linewelder.medus.ui.screens.createtask

import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.components.*
import io.github.linewelder.medus.ui.screens.createtask.CreateTaskViewModel.*
import io.github.linewelder.medus.ui.theme.*

/** New task form with an area picker. */
@Composable
fun CreateTaskScreen(
    onClose: () -> Unit,
    initialAreaId: String? = null,
    viewModel: CreateTaskViewModel = hiltViewModel(),
) {
    val areas by viewModel.areas.collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedAreaId by viewModel.selectedAreaId.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()
    BackHandler(enabled = saveState.blocksEditing) {}
    LaunchedEffect(saveState) {
        if (saveState == SaveState.Saved) onClose()
    }

    CreateTaskScreenContent(
        areas,
        saveState,
        onClose,
        selectedAreaId = selectedAreaId ?: initialAreaId,
        onSelectArea = viewModel::selectArea,
        onCreateArea = viewModel::createArea,
        onSaveTask = viewModel::saveTask,
    )
}

@Composable
private fun CreateTaskScreenContent(
    areas: List<Area>,
    saveState: SaveState,
    onClose: () -> Unit,
    selectedAreaId: String?,
    onSelectArea: (Area) -> Unit,
    onCreateArea: (String) -> Unit,
    onSaveTask: (String, Id<Area>, Int?, Int?) -> Unit,
) {
    var title by rememberSaveable { mutableStateOf("") }
    var timeEstimateMins by rememberSaveable { mutableStateOf<Int?>(null) }
    var priceEstimate by rememberSaveable { mutableStateOf<Int?>(null) }
    val areaId = areas.firstOrNull { it.id.toString() == selectedAreaId }?.id
    val editing = !saveState.blocksEditing
    val titleFocusRequester = remember { FocusRequester() }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.create_task), style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.task_title)) },
                enabled = editing,
                singleLine = true,
                modifier = Modifier.fillMaxWidth().focusRequester(titleFocusRequester),
            )
            LaunchedEffect(Unit) {
                if (editing) titleFocusRequester.requestFocus()
            }
            TaskAreaPicker(
                areas = areas,
                selectedAreaId = areaId,
                enabled = editing,
                onSelectArea = onSelectArea,
                onCreateArea = onCreateArea,
            )
            TaskTimeEstimatePicker(
                timeEstimateMins = timeEstimateMins,
                enabled = editing,
                onTimeEstimateChange = { timeEstimateMins = it },
            )
            TaskPriceEstimatePicker(
                priceEstimate = priceEstimate,
                enabled = editing,
                onPriceEstimateChange = { priceEstimate = it },
            )
            val errorMessage = when (saveState) {
                SaveState.Failed -> R.string.save_task_error
                SaveState.AreaCreationFailed -> R.string.create_area_error
                else -> null
            }
            if (errorMessage != null) {
                Text(stringResource(errorMessage), color = MaterialTheme.colorScheme.error)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                TextButton(onClick = onClose, enabled = editing) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = {
                        if (areaId != null) onSaveTask(title, areaId, timeEstimateMins, priceEstimate)
                    },
                    enabled = editing && title.isNotBlank() && areaId != null,
                ) {
                    Text(stringResource(R.string.save_task))
                }
            }
        }
    }
}

@Preview
@Composable
private fun CreateTaskScreenPreview() {
    MedusTheme(dynamicColor = false) {
        CreateTaskScreenContent(
            areas = listOf(Area(Id.empty(), "Errands")),
            saveState = SaveState.Editing,
            onClose = {},
            selectedAreaId = null,
            onSelectArea = {},
            onCreateArea = {},
            onSaveTask = { _, _, _, _ -> },
        )
    }
}
