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
    MenuScreenContent(
        areas = areas,
        onMove = { area, newIndex -> viewModel.moveArea(area.id, newIndex) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuScreenContent(
    areas: List<Area>,
    onMove: (Area, Int) -> Unit,
) {
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

                    if (areas.size > 1) {
                        AreaDragHandle(area, dragHandle)
                    }
                }
            }
        }
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
        MenuScreenContent(areas = areas, onMove = { _, _ -> })
    }
}
