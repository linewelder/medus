package io.github.linewelder.medus

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import dagger.hilt.android.*
import io.github.linewelder.medus.ui.*
import io.github.linewelder.medus.ui.components.*
import io.github.linewelder.medus.ui.theme.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedusTheme {
                ConfettiOverlay {
                    MedusNavigation()
                }
            }
        }
    }
}
