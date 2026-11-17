package io.github.linewelder.medus.ui.screens.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import androidx.lifecycle.compose.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.components.*
import io.github.linewelder.medus.ui.theme.*
import java.util.UUID

@Composable
fun MenuScreen(viewModel: MenuViewModel = hiltViewModel()) {
    val areas by viewModel.areas.collectAsStateWithLifecycle(initialValue = emptyList())
    val emptyAreaIds by viewModel.emptyAreaIds.collectAsStateWithLifecycle(initialValue = emptySet())
    MenuScreenContent(
        areas = areas,
        emptyAreaIds = emptyAreaIds,
        onMove = { area, newIndex -> viewModel.moveArea(area.id, newIndex) },
        onDeleteArea = viewModel::deleteEmptyArea,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuScreenContent(
    areas: List<Area>,
    emptyAreaIds: Set<Id<Area>>,
    onMove: (Area, Int) -> Unit,
    onDeleteArea: (Id<Area>) -> Unit,
) {
    var pendingDeletionId by remember { mutableStateOf<Id<Area>?>(null) }
    val areaToDelete = areas.firstOrNull { it.id == pendingDeletionId && it.id in emptyAreaIds }

    Scaffold( modifier = Modifier.fillMaxSize()) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .consumeWindowInsets(padding)
        ) {
            Text(
                stringResource(R.string.areas),
                style = MaterialTheme.typography.titleLarge,
            )

            ReorderableList(
                items = areas,
                key = { it.id.toString() },
                onMove = onMove,
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(padding),
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
                                contentDescription = stringResource(R.string.delete_area_named, area.name),
                            )
                        }
                    }
                    if (areas.size > 1) {
                        AreaDragHandle(area, dragHandle)
                    }
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

@Preview
@Composable
private fun MenuScreenPreview() {
    val areas = listOf(
        Area(Id(UUID(0, 1)), "Errands"),
        Area(Id(UUID(0, 2)), "Home"),
        Area(Id(UUID(0, 3)), "Work"),
    )

    MedusTheme(dynamicColor = false) {
        MenuScreenContent(
            areas = areas,
            emptyAreaIds = setOf(areas.first().id),
            onMove = { _, _ -> },
            onDeleteArea = {},
        )
    }
}
