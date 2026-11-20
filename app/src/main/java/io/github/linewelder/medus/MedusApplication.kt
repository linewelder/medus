package io.github.linewelder.medus

import android.app.*
import dagger.hilt.android.*
import io.github.linewelder.medus.core.data.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltAndroidApp
class MedusApplication : Application() {
    @Inject lateinit var databaseSeeder: DatabaseSeeder

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            databaseSeeder.seed()
        }
    }
}
