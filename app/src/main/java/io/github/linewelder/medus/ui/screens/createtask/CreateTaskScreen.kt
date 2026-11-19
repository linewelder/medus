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

/** Display a task form with a choice of an existing area or an inline area name. */
@Composable
fun CreateTaskScreen(
    onClose: () -> Unit,
    initialAreaId: String? = null,
    viewModel: CreateTaskViewModel = hiltViewModel(),
) {
    val areas by viewModel.areas.collectAsStateWithLifecycle(initialValue = emptyList())
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()
    BackHandler(enabled = saveState == SaveState.Saving || saveState == SaveState.Saved) {}
    LaunchedEffect(saveState) {
        if (saveState == SaveState.Saved) onClose()
    }

    CreateTaskScreenContent(
        areas,
        saveState,
        onClose,
        initialAreaId = initialAreaId,
        onSaveTask = viewModel::saveTask,
    )
}

@Composable
private fun CreateTaskScreenContent(
    areas: List<Area>,
    saveState: SaveState,
    onClose: () -> Unit,
    initialAreaId: String?,
    onSaveTask: (String, Area?, String, Int?) -> Unit,
) {
    var title by rememberSaveable { mutableStateOf("") }
    var selectedAreaId by rememberSaveable { mutableStateOf(initialAreaId) }
    var creatingArea by rememberSaveable { mutableStateOf(false) }
    var areaName by rememberSaveable { mutableStateOf("") }
    var timeEstimateMins by rememberSaveable { mutableStateOf<Int?>(null) }
    val selectedArea = areas.firstOrNull { it.id.toString() == selectedAreaId }
    val editing = saveState == SaveState.Editing || saveState == SaveState.Failed
    val hasArea = if (creatingArea) areaName.isNotBlank() else selectedArea != null
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
                selectedArea = selectedArea,
                creatingArea = creatingArea,
                areaName = areaName,
                enabled = editing,
                onSelectArea = {
                    selectedAreaId = it.id.toString()
                    creatingArea = false
                },
                onCreateArea = { creatingArea = true },
                onAreaNameChange = { areaName = it },
            )
            TaskTimeEstimatePicker(
                timeEstimateMins = timeEstimateMins,
                enabled = editing,
                onTimeEstimateChange = { timeEstimateMins = it },
            )
            if (saveState == SaveState.Failed) {
                Text(stringResource(R.string.save_task_error), color = MaterialTheme.colorScheme.error)
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
                        onSaveTask(title, selectedArea.takeUnless { creatingArea }, areaName, timeEstimateMins)
                    },
                    enabled = editing && title.isNotBlank() && hasArea,
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
            initialAreaId = null,
            onSaveTask = { _, _, _, _ -> },
        )
    }
}
