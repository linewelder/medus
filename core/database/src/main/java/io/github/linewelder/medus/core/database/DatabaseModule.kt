package io.github.linewelder.medus.core.database

import android.content.*
import androidx.room.*
import dagger.*
import dagger.hilt.*
import dagger.hilt.android.qualifiers.*
import dagger.hilt.components.*
import io.github.linewelder.medus.core.database.migrations.*
import javax.inject.*

/** Supplies the application-wide Room database. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideMedusDatabase(@ApplicationContext context: Context): MedusDatabase =
        Room.databaseBuilder(context, MedusDatabase::class.java, "medus.db")
            .addMigrations(
                migration2To3,
                migration3To4,
                migration4To5,
                migration5To6,
                migration6To7,
            )
            .fallbackToDestructiveMigrationFrom(true, 1)
            .build()
}
