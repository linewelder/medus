package io.github.linewelder.medus.core.data

import androidx.room.*
import io.github.linewelder.medus.core.database.*
import io.github.linewelder.medus.core.database.model.*
import io.github.linewelder.medus.core.model.*
import javax.inject.*
import kotlin.time.*

class DatabaseSeeder @Inject constructor(
    private val database: MedusDatabase,
) {
    suspend fun seed() = database.withTransaction {
        val seedingDate = Clock.System.now()

        val checklistDao = database.checklistDao()
        for ((role, id) in checklistIds) {
            if (!checklistDao.existsByRole(role)) {
                val checklist = ChecklistEntity(id, seedingDate, role)
                checklistDao.insert(checklist)
            }
        }
    }

    private companion object {
        val checklistIds = mapOf(
            ChecklistRole.Morning to Id.fromString<Checklist>("3903ee55-b580-4924-9f13-51379e7cffd0"),
            ChecklistRole.Evening to Id.fromString<Checklist>("3903ee55-b580-4924-9f13-51379e7cffd1"),
        )
    }
}
