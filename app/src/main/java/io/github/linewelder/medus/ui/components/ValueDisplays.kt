package io.github.linewelder.medus.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import io.github.linewelder.medus.R

@Composable
fun formatDurationMinutes(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours <= 0 -> stringResource(R.string.duration_minutes, totalMinutes)
        minutes == 0 -> stringResource(R.string.duration_hours, hours)
        else -> stringResource(R.string.duration_hours_minutes, hours, minutes)
    }
}
