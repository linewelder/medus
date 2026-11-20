package io.github.linewelder.medus.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Replace the task completion flag with a nullable timestamp, using the Unix epoch for completed tasks. */
internal val migration2To3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE tasks_new (
                id BLOB NOT NULL PRIMARY KEY,
                title TEXT NOT NULL,
                dateCreated INTEGER NOT NULL,
                dateCompleted INTEGER,
                areaId BLOB,
                FOREIGN KEY (areaId) REFERENCES areas (id) ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO tasks_new (id, title, dateCreated, dateCompleted, areaId)
            SELECT id, title, dateCreated, CASE WHEN completed != 0 THEN 0 END, areaId
            FROM tasks
            """.trimIndent()
        )
        db.execSQL("DROP TABLE tasks")
        db.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
        db.execSQL("CREATE INDEX index_tasks_areaId ON tasks (areaId)")
    }
}
