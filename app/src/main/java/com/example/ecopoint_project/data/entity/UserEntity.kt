package com.example.ecopoint_project.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1, // ID 1 karena kita cuma butuh single user
    val name: String,
    val totalPoints: Int = 0,
    val currentLevel: String = "Warga Baru"
)