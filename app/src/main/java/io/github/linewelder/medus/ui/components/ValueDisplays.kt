package io.github.linewelder.medus.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import java.text.*
import java.util.*

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

@Composable
fun ChecklistRole.displayName(): String = stringResource(
    when (this) {
        ChecklistRole.Morning -> R.string.checklist_morning
        ChecklistRole.Evening -> R.string.checklist_evening
    },
)

fun formatPrice(amount: Int): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pl-PL")).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }.format(amount)
