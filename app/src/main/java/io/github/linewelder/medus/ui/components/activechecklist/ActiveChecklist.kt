package io.github.linewelder.medus.ui.components.activechecklist

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.hilt.lifecycle.viewmodel.compose.*
import io.github.linewelder.medus.R
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.components.*
import io.github.linewelder.medus.ui.theme.*
import kotlin.time.*

/**
 * UI for going through a checklist in real time.
 */
@Composable
fun ActiveChecklist(
    checklist: Checklist,
    completionReset: Instant,
    modifier: Modifier = Modifier,
) {
    val viewModel: ActiveChecklistViewModel = hiltViewModel()
    ActiveChecklistContent(
        checklist = checklist,
        completionReset = completionReset,
        onCheckedChange = viewModel::setItemCompleted,
        modifier = modifier,
    )
}

@Composable
internal fun ActiveChecklistContent(
    checklist: Checklist,
    completionReset: Instant,
    onCheckedChange: (Id<ChecklistItem>, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val oneItemLeft = checklist.items.count { !it.isCompleted(completionReset) } == 1

    Column(modifier) {
        Text(
            checklist.role?.displayName() ?: stringResource(R.string.checklist),
            style = MaterialTheme.typography.titleLarge,
        )

        checklist.items.forEach { item ->
            key(item.id) {
                val checked = item.isCompleted(completionReset)

                val confetti = rememberConfettiAnchor()
                Row(
                    modifier = Modifier
                        .toggleable(
                            value = checked,
                            role = Role.Checkbox,
                            onValueChange = { newChecked ->
                                if (newChecked && oneItemLeft) confetti.spawn()
                                onCheckedChange(item.id, newChecked)
                            },
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(item.title, modifier = Modifier.weight(1f))
                    Checkbox(
                        checked = checked,
                        onCheckedChange = null,
                        modifier = confetti.modifier
                            .semantics { contentDescription = item.title },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActiveChecklistPreview() {
    val dayStart = Instant.parse("2026-11-21T00:00:00Z")
    val checkedAt = Instant.parse("2026-11-21T08:00:00Z")
    var checklist by remember {
        mutableStateOf(
            Checklist(
                id = Id.dummy(1),
                dateCreated = dayStart,
                role = ChecklistRole.Morning,
                items = listOf(
                    ChecklistItem(Id.dummy(2), "Drink a glass of water", checkedAt),
                    ChecklistItem(Id.dummy(3), "Review today's tasks"),
                ),
            ),
        )
    }

    MedusTheme(dynamicColor = false) {
        ConfettiOverlay {
            ActiveChecklistContent(
                checklist = checklist,
                completionReset = dayStart,
                onCheckedChange = { id, checked ->
                    checklist = checklist.copy(items = checklist.items.map { item ->
                        if (item.id == id) {
                            item.copy(dateCompleted = checkedAt.takeIf { checked })
                        } else {
                            item
                        }
                    })
                },
            )
        }
    }
}
