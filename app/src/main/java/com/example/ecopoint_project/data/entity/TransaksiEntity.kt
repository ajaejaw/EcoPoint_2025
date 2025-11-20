package com.example.ecopoint_project.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaksi_sampah")
data class TransaksiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // --- KOLOM BARU (PENTING UNTUK LOGIN) ---
    val userId: Int, // Menyimpan ID User pemilik sampah ini
    // ----------------------------------------

    val trashType: String, // Jenis sampah (Plastik/Kertas)
    val weightInKg: Double, // Berat
    val earnedPoints: Int, // Poin yang didapat
    val date: Long = System.currentTimeMillis() // Tanggal otomatis
)