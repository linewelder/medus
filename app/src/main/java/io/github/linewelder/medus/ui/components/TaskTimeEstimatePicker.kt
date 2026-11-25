package io.github.linewelder.medus.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val label = stringResource(R.string.task_time_estimate)
    val noEstimate = stringResource(R.string.task_time_estimate_none)

    WheelPickerField(
        value = timeEstimateMins,
        onValueChange = onTimeEstimateChange,
        options = timeEstimateOptions,
        label = { Text(label) },
        itemDisplay = { it?.let { formatDurationMinutes(it) } ?: noEstimate },
        sortComparator = nullsFirst(naturalOrder()),
        enabled = enabled,
    )
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
