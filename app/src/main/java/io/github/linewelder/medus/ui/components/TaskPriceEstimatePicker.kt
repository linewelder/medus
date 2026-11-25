package io.github.linewelder.medus.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.ui.theme.*

private val priceEstimateOptions = listOf(null, 10, 20, 30, 50, 100, 200, 300, 500)

/** Select a preset price estimate or clear it. */
@Composable
fun TaskPriceEstimatePicker(
    priceEstimate: Int?,
    enabled: Boolean,
    onPriceEstimateChange: (Int?) -> Unit,
) {
    val label = stringResource(R.string.task_price_estimate)
    val noValue = stringResource(R.string.no_value)

    WheelPickerField(
        value = priceEstimate,
        onValueChange = onPriceEstimateChange,
        options = priceEstimateOptions,
        label = { Text(label) },
        itemDisplay = { it?.let { formatPrice(it) } ?: noValue },
        sortComparator = nullsFirst(naturalOrder()),
        enabled = enabled,
    )
}

@Preview
@Composable
private fun TaskPriceEstimatePickerPreview() {
    MedusTheme {
        var estimate by remember { mutableStateOf<Int?>(20) }
        TaskPriceEstimatePicker(
            priceEstimate = estimate,
            enabled = true,
            onPriceEstimateChange = { estimate = it },
        )
    }
}
