package com.example.ecopoint_project.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecopoint_project.data.entity.TransaksiEntity
import com.example.ecopoint_project.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EcoDao {
    // --- BAGIAN USER (GAMIFIKASI) ---

    // Ambil data user (Menggunakan Flow agar data selalu update otomatis di UI)
    @Query("SELECT * FROM user_table WHERE id = 1")
    fun getUser(): Flow<UserEntity?>

    // Simpan user baru (hanya dijalankan sekali saat instal)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity)

    // Update Poin & Level (Logika Gamifikasi)
    @Query("UPDATE user_table SET totalPoints = :points, currentLevel = :level WHERE id = 1")
    suspend fun updateUserPoints(points: Int, level: String)

    // --- BAGIAN TRANSAKSI (RIWAYAT) ---

    // Simpan transaksi sampah baru (Create)
    @Insert
    suspend fun insertTransaksi(transaksi: TransaksiEntity)

    // Ambil semua riwayat, urutkan dari yang terbaru (Read)
    @Query("SELECT * FROM transaksi_sampah ORDER BY date DESC")
    fun getAllTransaksi(): Flow<List<TransaksiEntity>>

    // Hapus semua riwayat (Opsional/Delete)
    @Query("DELETE FROM transaksi_sampah")
    suspend fun deleteAllTransaksi()
}