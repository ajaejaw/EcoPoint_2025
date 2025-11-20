package com.example.ecopoint_project.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    // --- INI YANG HILANG TADI ---
    val username: String,  // Kolom username (penyebab error)
    val password: String,  // Kolom password
    // ----------------------------

    val totalPoints: Int = 0,
    val currentLevel: String = "Warga Baru"
)