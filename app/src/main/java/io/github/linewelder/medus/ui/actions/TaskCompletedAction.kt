package io.github.linewelder.medus.ui.actions

import android.content.*
import io.github.linewelder.medus.*
import io.github.linewelder.medus.core.data.repositories.*
import io.github.linewelder.medus.core.model.*
import io.github.linewelder.medus.ui.*

internal class TaskCompletedAction(
    private val repository: TaskRepository,
    private val task: Task,
) : Action() {
    override fun display(context: Context): String =
        context.getString(R.string.action_task_completed, task.title)

    override suspend fun undo() {
        repository.setTaskCompleted(task.id, false)
    }
}
