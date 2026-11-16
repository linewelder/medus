package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*

/** Display a task with a completion checkbox and strike through completed titles. */
@Composable
fun TaskListItem(task: Task, onEdit: () -> Unit, onCompletedChange: (Boolean) -> Unit) {
    val completed = task.dateCompleted != null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = task.title,
            modifier = Modifier
                .weight(1f)
                .clickable(role = Role.Button, onClick = onEdit)
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (completed) TextDecoration.LineThrough else null,
            color = if (completed) MaterialTheme.colorScheme.secondary else Color.Unspecified,
        )
        val completionLabel = stringResource(R.string.task_completion_for_title, task.title)
        Box(
            modifier = Modifier
                .sizeIn(minWidth = 56.dp, minHeight = 48.dp)
                .toggleable(
                    value = completed,
                    role = Role.Checkbox,
                    onValueChange = onCompletedChange,
                )
                .semantics { contentDescription = completionLabel },
            contentAlignment = Alignment.Center,
        ) {
            Checkbox(checked = completed, onCheckedChange = null)
        }
    }
}
