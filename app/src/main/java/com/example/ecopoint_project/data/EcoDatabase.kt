package com.example.ecopoint_project.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ecopoint_project.data.dao.EcoDao
import com.example.ecopoint_project.data.entity.TransaksiEntity
import com.example.ecopoint_project.data.entity.UserEntity

// Daftar tabel didaftarkan di sini [cite: 30, 31]
@Database(entities = [UserEntity::class, TransaksiEntity::class], version = 1, exportSchema = false)
abstract class EcoDatabase : RoomDatabase() {

    // Pintu akses ke DAO
    abstract fun ecoDao(): EcoDao

    companion object {
        @Volatile
        private var INSTANCE: EcoDatabase? = null

        // Fungsi untuk membuat/mengambil database
        fun getDatabase(context: Context): EcoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EcoDatabase::class.java,
                    "ecopoint_database" // Nama file database di HP
                )
                    .fallbackToDestructiveMigration() // Reset DB jika ada perubahan struktur (aman buat development)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}