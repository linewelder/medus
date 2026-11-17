package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import sh.calvin.reorderable.*

/** Display a list with drag handles and report the moved item and its offset on drop. */
@Composable
fun <T> ReorderableList(
    items: List<T>,
    key: (T) -> String,
    onMove: (item: T, newIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable (item: T, dragHandleModifier: Modifier) -> Unit,
) {
    var displayedItems by remember(items) { mutableStateOf(items) }
    var awaitingSavedOrder by remember(items) { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(listState) { from, to ->
        val fromIndex = displayedItems.indexOfFirst { key(it) == from.key }
        val toIndex = displayedItems.indexOfFirst { key(it) == to.key }
        val canMove = fromIndex >= 0 && toIndex >= 0 && fromIndex != toIndex
        if (canMove) {
            displayedItems = displayedItems.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = verticalArrangement,
    ) {
        items(displayedItems, key = key) { item ->
            ReorderableItem(reorderState, key = key(item)) {
                val handle = Modifier.draggableHandle(
                    enabled = items.size > 1 && !awaitingSavedOrder,
                    onDragStopped = {
                        val originalIndex = items.indexOf(item)
                        val newIndex = displayedItems.indexOf(item)
                        if (newIndex == originalIndex) return@draggableHandle

                        awaitingSavedOrder = true
                        onMove(item, newIndex)
                    },
                )
                content(item, handle)
            }
        }
    }
}
