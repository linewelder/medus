package io.github.linewelder.medus.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Assign existing areas positions using the previous alphabetical display order. */
internal val migration3To4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE areas ADD COLUMN sortOrder INTEGER NOT NULL DEFAULT 0")
        db.execSQL(
            """
            UPDATE areas
            SET sortOrder = (
                SELECT COUNT(*)
                FROM areas AS preceding
                WHERE preceding.name COLLATE NOCASE < areas.name
                    OR (preceding.name COLLATE NOCASE = areas.name AND preceding.id < areas.id)
            )
            """.trimIndent()
        )
    }
}
