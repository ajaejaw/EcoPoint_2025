package com.example.ecopoint_project.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaksi_sampah",
    // Menambahkan Foreign Key ke UserEntity
    // Artinya: Jika User dihapus, semua data sampah miliknya juga ikut terhapus (CASCADE)
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransaksiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: Int,

    val trashType: String,
    val weightInKg: Double,
    val earnedPoints: Int,
    val date: Long = System.currentTimeMillis()
)