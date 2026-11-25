package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.ui.theme.*

private val timeEstimateOptions = listOf(null, 5, 10, 15, 20, 30, 40, 60)

/** Select a preset estimate or clear the current estimate. */
@Composable
fun TaskTimeEstimatePicker(
    timeEstimateMins: Int?,
    enabled: Boolean,
    onTimeEstimateChange: (Int?) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val label = stringResource(R.string.task_time_estimate)
    val noEstimate = stringResource(R.string.task_time_estimate_none)
    val estimate = timeEstimateMins?.let { formatDurationMinutes(it) } ?: noEstimate

    FakeOutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = estimate,
        label = { Text(label) },
        onClick = if (enabled) ({ showDialog = true }) else null,
    )

    if (enabled && showDialog) {
        val options = remember(timeEstimateMins) {
            (timeEstimateOptions + timeEstimateMins).distinct().sortedWith(nullsFirst(naturalOrder()))
        }
        var selectedEstimate by remember(timeEstimateMins) { mutableStateOf(timeEstimateMins) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(label) },
            text = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    WheelPicker(
                        value = selectedEstimate,
                        values = options,
                        onValueChange = { selectedEstimate = it },
                        display = { it?.let { minutes -> formatDurationMinutes(minutes) } ?: noEstimate },
                        textStyle = Typography.headlineSmall,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onTimeEstimateChange(selectedEstimate)
                }) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Preview
@Composable
private fun TaskTimeEstimatePickerPreview() {
    MedusTheme {
        var estimate by remember { mutableStateOf<Int?>(20) }
        TaskTimeEstimatePicker(
            timeEstimateMins = estimate,
            enabled = true,
            onTimeEstimateChange = { estimate = it },
        )
    }
}
