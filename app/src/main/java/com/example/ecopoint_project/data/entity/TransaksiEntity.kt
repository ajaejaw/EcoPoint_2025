package com.example.ecopoint_project.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaksi_sampah")
data class TransaksiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val trashType: String,
    val weightInKg: Double,
    val earnedPoints: Int,
    val date: Long = System.currentTimeMillis()
)