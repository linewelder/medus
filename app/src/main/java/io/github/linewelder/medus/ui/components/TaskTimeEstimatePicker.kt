package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import io.github.linewelder.medus.R

private val timeEstimateOptions = listOf(null, 5, 10, 15, 20, 30, 40, 60)

/** Select a preset estimate or clear the current estimate. */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaskTimeEstimatePicker(
    timeEstimateMins: Int?,
    enabled: Boolean,
    onTimeEstimateChange: (Int?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val noEstimate = stringResource(R.string.task_time_estimate_none)
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = if (timeEstimateMins == null) noEstimate else formatDurationMinutes(timeEstimateMins),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            singleLine = true,
            label = { Text(stringResource(R.string.task_time_estimate)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = enabled)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            for (minutes in timeEstimateOptions) {
                DropdownMenuItem(
                    text = { Text(if (minutes == null) noEstimate else formatDurationMinutes(minutes)) },
                    onClick = {
                        expanded = false
                        onTimeEstimateChange(minutes)
                    },
                )
            }
        }
    }
}
