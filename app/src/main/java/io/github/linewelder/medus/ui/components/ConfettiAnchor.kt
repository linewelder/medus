package io.github.linewelder.medus.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.layout.*

@Composable
fun rememberConfettiAnchor(): ConfettiAnchor {
    val confettiState = LocalConfettiState.current
    return remember { ConfettiAnchor(confettiState) }
}

/**
 * Utility class to emit confetti from a composable
 */
class ConfettiAnchor internal constructor(private val confettiState: ConfettiState) {
    private var _confettiOrigin = Offset(0f, 0f)

    /**
     * Put this modifier on the source of confetti
     */
    val modifier: Modifier = Modifier.onGloballyPositioned {
        val halfSize = it.size / 2
        _confettiOrigin = confettiState.canvasCoordinates.localPositionOf(
            it,
            Offset(
                halfSize.width.toFloat(),
                halfSize.height.toFloat(),
            ),
        )
    }

    fun spawn() {
        confettiState.spawn(_confettiOrigin)
    }
}
