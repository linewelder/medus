package io.github.linewelder.medus.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Supplies the application-wide Room database. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /** Build the persistent database once its Room schema is defined. */
    @Provides
    @Singleton
    fun provideMedusDatabase(@ApplicationContext context: Context): MedusDatabase =
        Room.databaseBuilder(context, MedusDatabase::class.java, "medus.db").build()
}
