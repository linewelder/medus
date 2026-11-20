package io.github.linewelder.medus.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import kotlin.time.*

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
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(role = Role.Button, onClick = onEdit)
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = task.title,
                modifier = Modifier.weight(1f, fill = false),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (completed) TextDecoration.LineThrough else null,
                color = if (completed) MaterialTheme.colorScheme.secondary else Color.Unspecified,
            )

            if (!completed) {
                task.timeEstimateMins?.let { TimeEstimateLabel(it) }
            }
        }
        val completionLabel = stringResource(R.string.task_completion_for_title, task.title)
        ConfettiCheckbox(
            checked = completed,
            modifier = Modifier
                .sizeIn(minWidth = 56.dp, minHeight = 48.dp)
                .semantics { contentDescription = completionLabel },
            onCheckedChange = onCompletedChange,
        )
    }
}

@Composable
private fun TimeEstimateLabel(minutes: Int) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Text(
            text = formatDurationMinutes(minutes),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelMedium,
            softWrap = false,
        )
    }
}

@Preview
@Composable
private fun TaskListItemPreview() {
    var completed by remember { mutableStateOf(false) }

    ConfettiOverlay {
        TaskListItem(
            task = Task(
                id = Id.empty(),
                title = "Do Homework",
                timeEstimateMins = 60,
                dateCreated = Instant.fromEpochMilliseconds(0),
                dateCompleted = if (completed) Instant.fromEpochMilliseconds(0) else null,
            ),
            onEdit = { },
            onCompletedChange = { completed = it },
        )
    }
}
