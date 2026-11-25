package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.ui.theme.*

/**
 * Opens a dialog with a [WheelPicker] when pressed.
 * Allows for input of a value from a set of options.
 *
 * When [value] is not in [options], it is added to the option list,
 * and if [sortComparator] is provided, it is inserted in the right order.
 */
@Composable
fun <T> WheelPickerField(
    value: T,
    onValueChange: (T) -> Unit,
    options: List<T>,
    label: @Composable () -> Unit,
    itemDisplay: @Composable (T) -> String,
    sortComparator: Comparator<T>? = null,
    enabled: Boolean = true,
) {
    var showDialog by remember { mutableStateOf(false) }

    FakeOutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = itemDisplay(value),
        label = label,
        onClick = if (enabled) ({ showDialog = true }) else null,
    )

    if (enabled && showDialog) {
        val options = remember(value) {
            val allOptions = (options + value).distinct()
            if (sortComparator != null) {
                allOptions.sortedWith(sortComparator)
            } else {
                allOptions
            }
        }
        var wheelSelection by remember(value) { mutableStateOf(value) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = label,
            text = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    WheelPicker(
                        value = value,
                        values = options,
                        onValueChange = { wheelSelection = it },
                        display = itemDisplay,
                        textStyle = Typography.headlineSmall,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onValueChange(wheelSelection)
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
