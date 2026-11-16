package io.github.linewelder.medus.ui.createtask

import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.ui.TaskAreaPicker
import io.github.linewelder.medus.ui.createtask.CreateTaskViewModel.SaveState

/** Display a task form with a choice of an existing area or an inline area name. */
@Composable
fun CreateTaskScreen(
    onClose: () -> Unit,
    viewModel: CreateTaskViewModel = hiltViewModel(),
) {
    val areas by viewModel.areas.collectAsStateWithLifecycle(initialValue = emptyList())
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()
    var title by rememberSaveable { mutableStateOf("") }
    var selectedAreaId by rememberSaveable { mutableStateOf<String?>(null) }
    var creatingArea by rememberSaveable { mutableStateOf(false) }
    var areaName by rememberSaveable { mutableStateOf("") }
    val selectedArea = areas.firstOrNull { it.id.toString() == selectedAreaId }
    val editing = saveState == SaveState.Editing || saveState == SaveState.Failed
    val hasArea = if (creatingArea) areaName.isNotBlank() else selectedArea != null

    BackHandler(enabled = !editing) {}
    LaunchedEffect(saveState) {
        if (saveState == SaveState.Saved) onClose()
    }

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
                modifier = Modifier.fillMaxWidth(),
            )
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
                    onClick = { viewModel.saveTask(title, selectedArea.takeUnless { creatingArea }, areaName) },
                    enabled = editing && title.isNotBlank() && hasArea,
                ) {
                    Text(stringResource(R.string.save_task))
                }
            }
        }
    }
}
