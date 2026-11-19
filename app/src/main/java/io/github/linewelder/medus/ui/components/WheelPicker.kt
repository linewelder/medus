package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.swmansion.kmpwheelpicker.*
import io.github.linewelder.medus.ui.theme.*
import kotlin.math.*

/**
 * Vertical wheel showing the selected value in a highlighted window between its neighbors.
 *
 * @param value Currently selected value. If it is not in [values], the first value is shown
 * as selected.
 * @param onValueChange Called when the user scrolls to a different value. It is called once
 * the wheel starts settling on the value, before the snapping animation finishes.
 * @param display Text shown for each value
 */
@Composable
fun <T> WheelPicker(
    value: T,
    values: List<T>,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    display: @Composable (T) -> String = { it.toString() },
    textStyle: TextStyle = LocalTextStyle.current,
) {
    val selectedIndex = values.indexOf(value).coerceAtLeast(0)
    val state = rememberWheelPickerState(itemCount = values.size, initialIndex = selectedIndex)

    val currentValue by rememberUpdatedState(value)
    val currentValues by rememberUpdatedState(values)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    LaunchedEffect(state) {
        snapshotFlow { state.index }.collect { index ->
            val pickedValue = currentValues.getOrNull(index) ?: return@collect
            if (pickedValue != currentValue) currentOnValueChange(pickedValue)
        }
    }

    LaunchedEffect(selectedIndex) {
        if (state.index != selectedIndex) state.scrollTo(selectedIndex)
    }

    WheelPicker(
        state = state,
        modifier = modifier,
        bufferSize = 1,
        window = {
            Box(
                Modifier.background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.shapes.small,
                )
            )
        },
        friction = 4f,
    ) { index ->
        val distanceFromWindow by remember(state, index) {
            derivedStateOf { abs(state.value - index).coerceIn(0f, 1f) }
        }
        Text(
            text = display(values[index]),
            style = textStyle,
            color = lerp(
                MaterialTheme.colorScheme.onSecondaryContainer,
                MaterialTheme.colorScheme.onSurfaceVariant,
                distanceFromWindow,
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Preview
@Composable
private fun WheelPickerPreview() {
    MedusTheme {
        var value by remember { mutableIntStateOf(5) }
        WheelPicker(
            value = value,
            values = (1..99).toList(),
            onValueChange = { value = it },
        )
    }
}
