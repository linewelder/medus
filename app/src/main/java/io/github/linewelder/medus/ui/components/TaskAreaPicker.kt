package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*

/** Offer existing areas and an inline name field for a new area. */
@Composable
internal fun TaskAreaPicker(
    areas: List<Area>,
    selectedArea: Area?,
    creatingArea: Boolean,
    areaName: String,
    enabled: Boolean,
    onSelectArea: (Area) -> Unit,
    onCreateArea: () -> Unit,
    onAreaNameChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        val areaLabel = when {
            creatingArea -> stringResource(R.string.new_area)
            selectedArea != null -> selectedArea.name
            else -> stringResource(R.string.choose_area)
        }
        OutlinedButton(onClick = { expanded = true }, enabled = enabled) {
            Text(areaLabel)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.new_area)) },
                onClick = {
                    expanded = false
                    onCreateArea()
                },
            )
            for (area in areas) {
                DropdownMenuItem(
                    text = { Text(area.name) },
                    onClick = {
                        expanded = false
                        onSelectArea(area)
                    },
                )
            }
        }
    }
    if (creatingArea) {
        OutlinedTextField(
            value = areaName,
            onValueChange = onAreaNameChange,
            label = { Text(stringResource(R.string.area_name)) },
            enabled = enabled,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
