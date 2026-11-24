package io.github.linewelder.medus.ui

import android.content.*
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.github.linewelder.medus.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.*

/**
 * Handles actions that can be undone.
 */
@Singleton
class ActionHistory @Inject constructor() {
    private val pendingActions = Channel<Action>(Channel.UNLIMITED)

    /** Undoable actions in commit order, consumed by the app's snackbar host. */
    val actions: Flow<Action> = pendingActions.receiveAsFlow()

    /** Register a new undoable action. */
    fun commit(action: Action) {
        check(pendingActions.trySend(action).isSuccess)
    }
}

/** An action that can be undone. */
abstract class Action {
    abstract fun display(context: Context): String
    abstract suspend fun undo()
}

/** Displays the undo prompts for undoable actions. */
@Composable
fun ActionHistorySnackbarHost(actionHistory: ActionHistory, modifier: Modifier = Modifier) {
    val hostState = remember { SnackbarHostState() }
    val context by rememberUpdatedState(LocalContext.current)
    val undoLabel by rememberUpdatedState(stringResource(R.string.undo))

    LaunchedEffect(actionHistory) {
        actionHistory.actions.collect { action ->
            val result = hostState.showSnackbar(
                message = action.display(context),
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short,
            )

            if (result == SnackbarResult.ActionPerformed) {
                action.undo()
            }
        }
    }

    SnackbarHost(hostState, modifier = modifier)
}
