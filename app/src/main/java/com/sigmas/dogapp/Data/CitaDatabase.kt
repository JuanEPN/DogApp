package com.sigmas.dogapp.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sigmas.dogapp.Data.Model.Cita

@Database(entities = [Cita::class], version = 1, exportSchema = false)
abstract class CitaDatabase : RoomDatabase() {

    abstract fun citaDao(): CitaDao

    companion object {
        @Volatile
        private var INSTANCE: CitaDatabase? = null

        fun getDatabase(context: Context): CitaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CitaDatabase::class.java,
                    "cita_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}