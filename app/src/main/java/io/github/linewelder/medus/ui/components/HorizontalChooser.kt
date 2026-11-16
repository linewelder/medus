package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

data class HorizontalChooserOption(val id: String, val label: String)

/** Display options as a horizontally scrollable row with a single selection. */
@Composable
fun HorizontalChooser(
    options: List<HorizontalChooserOption>,
    selectedOptionId: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
) {
    LazyRow(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(options, key = { it.id }) { option ->
            FilterChip(
                selected = option.id == selectedOptionId,
                onClick = { onOptionSelected(option.id) },
                label = { Text(option.label, maxLines = 1) },
            )
        }
    }
}
