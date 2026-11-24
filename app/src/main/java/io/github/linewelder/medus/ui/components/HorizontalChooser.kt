package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

/** Display options as a horizontally scrollable row with a single selection. */
@Composable
fun <T> HorizontalChooser(
    options: List<T>,
    selectedKey: Any,
    key: (T) -> Any,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    itemContent: @Composable (T) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(options, key = key) { option ->
            FilterChip(
                selected = key(option) == selectedKey,
                onClick = { onOptionSelected(option) },
                label = { itemContent(option) },
            )
        }
    }
}
