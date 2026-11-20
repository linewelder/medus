package io.github.linewelder.medus.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.layout.*

/**
 * Checkbox that spawns confetti when checked.
 * This behavior can be enabled/disabled by using [celebrateOnCheck].
 */
@Composable
fun ConfettiCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    celebrateOnCheck: Boolean = true,
) {
    var confettiOrigin by remember { mutableStateOf(Offset(0f, 0f)) }
    val confettiState = LocalConfettiState.current

    Checkbox(
        modifier = modifier.onGloballyPositioned {
            val halfSize = it.size / 2
            confettiOrigin = confettiState.canvasCoordinates.localPositionOf(
                it,
                Offset(
                    halfSize.width.toFloat(),
                    halfSize.height.toFloat(),
                ),
            )
        },
        checked = checked,
        onCheckedChange = { newChecked ->
            if (newChecked && celebrateOnCheck) {
                confettiState.spawn(confettiOrigin)
            }
            onCheckedChange(newChecked)
        },
    )
}
