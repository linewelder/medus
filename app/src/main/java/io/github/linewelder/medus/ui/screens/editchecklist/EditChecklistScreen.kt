package io.github.linewelder.medus.ui.screens.editchecklist

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.components.*
import io.github.linewelder.medus.ui.theme.MedusTheme
import kotlin.time.Instant

@Composable
fun EditChecklistScreen(viewModel: EditChecklistViewModel = hiltViewModel()) {
    val checklist by viewModel.checklist.collectAsStateWithLifecycle(initialValue = null)

    EditChecklistScreenContent(
        checklist = checklist,
        onMove = viewModel::moveItem,
        onAdd = viewModel::addItem,
        onRename = viewModel::renameItem,
        onDelete = viewModel::deleteItem,
    )
}

@Composable
private fun EditChecklistScreenContent(
    checklist: Checklist?,
    onMove: (Id<ChecklistItem>, Int) -> Unit,
    onAdd: (String) -> Unit,
    onRename: (Id<ChecklistItem>, String) -> Unit,
    onDelete: (Id<ChecklistItem>) -> Unit,
) {
    Scaffold { padding ->
        val currentChecklist = checklist
        if (currentChecklist != null) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .imePadding()
                    .padding(16.dp),
            ) {
                Text(
                    currentChecklist.role?.displayName() ?: stringResource(R.string.checklist),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                val itemsById = currentChecklist.items.associateBy { it.id }
                ReorderableList(
                    items = currentChecklist.items.map { it.id },
                    key = { it.toString() },
                    onMove = onMove,
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) { id, dragHandle ->
                    ChecklistItemRow(
                        item = itemsById.getValue(id),
                        onRename = { onRename(id, it) },
                        onDelete = { onDelete(id) },
                        dragHandle = dragHandle,
                    )
                }
                NewChecklistItemRow(onAdd = onAdd)
            }
        }
    }
}

@Composable
private fun ChecklistItemRow(
    item: ChecklistItem,
    onRename: (String) -> Unit,
    onDelete: () -> Unit,
    dragHandle: Modifier,
) {
    var title by rememberSaveable { mutableStateOf(item.title) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                onRename(it)
            },
            label = { Text(stringResource(R.string.checklist_item)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onDelete) {
            Icon(
                painterResource(R.drawable.ic_delete),
                contentDescription = stringResource(R.string.delete_checklist_item),
            )
        }
        Box(
            modifier = dragHandle.size(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(R.drawable.ic_drag_handle),
                contentDescription = stringResource(R.string.reorder_checklist_item),
            )
        }
    }
}

@Composable
private fun NewChecklistItemRow(onAdd: (String) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.new_checklist_item)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = {
                onAdd(title)
                title = ""
            },
            enabled = title.isNotBlank(),
        ) {
            Icon(
                painterResource(R.drawable.ic_add),
                contentDescription = stringResource(R.string.add_checklist_item),
            )
        }
    }
}

@Preview
@Composable
private fun EditChecklistScreenPreview() {
    val checklist = Checklist(
        id = Id.dummy(1),
        dateCreated = Instant.fromEpochMilliseconds(0),
        role = ChecklistRole.Morning,
        items = listOf(
            ChecklistItem(Id.dummy(2), "Drink a glass of water"),
            ChecklistItem(Id.dummy(3), "Review today's tasks"),
        ),
    )

    MedusTheme(dynamicColor = false) {
        EditChecklistScreenContent(
            checklist = checklist,
            onMove = { _, _ -> },
            onAdd = {},
            onRename = { _, _ -> },
            onDelete = {},
        )
    }
}
