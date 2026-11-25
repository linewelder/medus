package io.github.linewelder.medus.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import io.github.linewelder.medus.ui.components.*

/** Screen preview with the app navigation bar and confetti overlay. */
@Composable
internal fun FullScreenPreview(
    selectedTab: BottomBarTab,
    content: @Composable () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            MedusBottomBar(selectedTab, { })
        },
    ) { padding ->
        Box(Modifier.padding(padding).consumeWindowInsets(padding)) {
            ConfettiOverlay {
                content() }
        }
    }
}
