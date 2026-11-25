package io.github.linewelder.medus.ui.components

import android.view.*
import android.widget.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.viewinterop.*

/**
 * Display the current time using the device's clock format.
 * @param previewTime Hardcode a specific value for previews.
 */
@Composable
fun CurrentTimeLabel(modifier: Modifier = Modifier, previewTime: String? = null) {
    if (previewTime != null) {
        Text(
            text = previewTime,
            modifier = modifier,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
        )
        return
    }

    val textSize = MaterialTheme.typography.displayLarge.fontSize.value
    val textColor = LocalContentColor.current.toArgb()

    AndroidView(
        factory = { context -> TextClock(context).apply { gravity = Gravity.CENTER } },
        modifier = modifier,
        update = { clock ->
            clock.textSize = textSize
            clock.setTextColor(textColor)
        },
    )
}
