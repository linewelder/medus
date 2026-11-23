package io.github.linewelder.medus.ui.screens.menu

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.components.*
import io.github.linewelder.medus.ui.theme.*
import kotlin.time.*

@Composable
fun MenuScreen(
    onEditChecklist: (Id<Checklist>) -> Unit,
    viewModel: MenuViewModel = hiltViewModel(),
) {
    val checklists by viewModel.checklists.collectAsStateWithLifecycle(initialValue = emptyList())
    val areas by viewModel.areas.collectAsStateWithLifecycle(initialValue = emptyList())
    val emptyAreaIds by viewModel.emptyAreaIds.collectAsStateWithLifecycle(initialValue = emptySet())
    MenuScreenContent(
        checklists = checklists,
        onEditChecklist = onEditChecklist,
        areas = areas,
        emptyAreaIds = emptyAreaIds,
        onMove = { area, newIndex -> viewModel.moveArea(area.id, newIndex) },
        onDeleteArea = viewModel::deleteEmptyArea,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuScreenContent(
    checklists: List<Checklist>,
    onEditChecklist: (Id<Checklist>) -> Unit,
    areas: List<Area>,
    emptyAreaIds: Set<Id<Area>>,
    onMove: (Area, Int) -> Unit,
    onDeleteArea: (Id<Area>) -> Unit,
) {
    Scaffold( modifier = Modifier.fillMaxSize()) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .consumeWindowInsets(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AreasSection(areas, onMove, emptyAreaIds, onDeleteArea)

            ChecklistSection(checklists, onEditChecklist)
        }
    }
}

@Composable
private fun AreasSection(
    areas: List<Area>,
    onMove: (Area, Int) -> Unit,
    emptyAreaIds: Set<Id<Area>>,
    onDeleteArea: (Id<Area>) -> Unit,
) {
    var pendingDeletionId by remember { mutableStateOf<Id<Area>?>(null) }
    val areaToDelete = areas.firstOrNull { it.id == pendingDeletionId && it.id in emptyAreaIds }

    Column(Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.areas),
            style = MaterialTheme.typography.titleLarge,
        )

        ReorderableList(
            items = areas,
            key = { it.id.toString() },
            onMove = onMove,
            modifier = Modifier.weight(1f, fill = false),
        ) { area, dragHandle ->
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    area.name,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                )

                if (area.id in emptyAreaIds) {
                    IconButton(
                        onClick = { pendingDeletionId = area.id },
                        modifier = Modifier.height(48.dp),
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_delete),
                            contentDescription = stringResource(
                                R.string.delete_area_named,
                                area.name
                            ),
                        )
                    }
                }
                if (areas.size > 1) {
                    AreaDragHandle(area, dragHandle)
                }
            }
        }
    }

    if (areaToDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDeletionId = null },
            title = { Text(stringResource(R.string.delete_area)) },
            text = { Text(stringResource(R.string.confirm_delete_area, areaToDelete.name)) },
            dismissButton = {
                TextButton(onClick = { pendingDeletionId = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDeletionId = null
                        onDeleteArea(areaToDelete.id)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(stringResource(R.string.delete_area))
                }
            },
        )
    }
}

@Composable
private fun AreaDragHandle(
    area: Area,
    modifier: Modifier,
) {
    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Icon(
            painterResource(R.drawable.ic_drag_handle),
            contentDescription = stringResource(R.string.reorder_area, area.name),
        )
    }
}

@Composable
private fun ChecklistSection(
    checklists: List<Checklist>,
    onEditChecklist: (Id<Checklist>) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.checklists), style = MaterialTheme.typography.titleLarge)
        checklists.forEach { checklist ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onEditChecklist(checklist.id) },
            ) {
                Text(checklist.role?.displayName() ?: stringResource(R.string.checklist))
            }
        }
    }
}

@Preview
@Composable
private fun MenuScreenPreview() {
    val areas = listOf(
        Area(Id.dummy(1), "Errands"),
        Area(Id.dummy(2), "Home"),
        Area(Id.dummy(3), "Work"),
    )

    val checklists = listOf(
        Checklist(
            id = Id.empty(),
            dateCreated = Instant.fromEpochMilliseconds(0),
            role = ChecklistRole.Morning,
            items = listOf(
                ChecklistItem(Id.empty(), "Brush teeth"),
            ),
        ),
        Checklist(
            id = Id.empty(),
            dateCreated = Instant.fromEpochMilliseconds(0),
            role = ChecklistRole.Evening,
            items = listOf(
                ChecklistItem(Id.empty(), "Brush teeth"),
            ),
        ),
    )

    MedusTheme(dynamicColor = false) {
        MenuScreenContent(
            checklists = checklists,
            onEditChecklist = {},
            areas = areas,
            emptyAreaIds = setOf(areas.first().id),
            onMove = { _, _ -> },
            onDeleteArea = {},
        )
    }
}
