package io.github.linewelder.medus.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Add checklists and their ordered items. */
internal val migration5To6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE checklists (
                id BLOB NOT NULL PRIMARY KEY,
                dateCreated INTEGER NOT NULL,
                role TEXT NULL
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE UNIQUE INDEX index_checklists_role ON checklists (role)")
        db.execSQL(
            """
            CREATE TABLE checklist_items (
                id BLOB NOT NULL PRIMARY KEY,
                title TEXT NOT NULL,
                dateCompleted INTEGER,
                checklistId BLOB NOT NULL,
                position INTEGER NOT NULL,
                FOREIGN KEY (checklistId) REFERENCES checklists (id) ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX index_checklist_items_checklistId ON checklist_items (checklistId)")
    }
}
