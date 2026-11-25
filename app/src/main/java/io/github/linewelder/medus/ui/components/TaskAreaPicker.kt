package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.theme.*

/** Wrapping area chips with a dialog for adding an area. */
@Composable
internal fun TaskAreaPicker(
    areas: List<Area>,
    selectedAreaId: Id<Area>?,
    enabled: Boolean,
    onSelectArea: (Area) -> Unit,
    onCreateArea: (String) -> Unit,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (area in areas) {
                key(area.id) {
                    FilterChip(
                        selected = area.id == selectedAreaId,
                        onClick = { onSelectArea(area) },
                        label = { Text(area.name) },
                        enabled = enabled,
                    )
                }
            }
            AssistChip(
                onClick = { showDialog = true },
                label = {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = stringResource(R.string.new_area),
                    )
                },
                enabled = enabled,
            )
        }
    }

    if (enabled && showDialog) {
        NewAreaDialog(
            onDismiss = { showDialog = false },
            onCreateArea = {
                showDialog = false
                onCreateArea(it)
            },
        )
    }
}

/** Name entry for a new area. */
@Composable
private fun NewAreaDialog(onDismiss: () -> Unit, onCreateArea: (String) -> Unit) {
    var areaName by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_area)) },
        text = {
            OutlinedTextField(
                value = areaName,
                onValueChange = { areaName = it },
                label = { Text(stringResource(R.string.area_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            )
            LaunchedEffect(Unit) { focusRequester.requestFocus() }
        },
        confirmButton = {
            TextButton(onClick = { onCreateArea(areaName.trim()) }, enabled = areaName.isNotBlank()) {
                Text(stringResource(R.string.create_area))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}

@Preview(showBackground = true, widthDp = 240)
@Composable
private fun TaskAreaPickerPreview() {
    val areas = listOf(Area(Id.dummy(1), "Errands"), Area(Id.dummy(2), "Home"), Area(Id.dummy(3), "Work"))
    MedusTheme(dynamicColor = false) {
        TaskAreaPicker(
            areas = areas,
            selectedAreaId = areas.first().id,
            enabled = true,
            onSelectArea = {},
            onCreateArea = {},
        )
    }
}
