package com.example.ecopoint_project.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecopoint_project.data.entity.TransaksiEntity
import com.example.ecopoint_project.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EcoDao {
    // --- AUTH ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_table WHERE username = :username AND password = :password")
    fun loginUser(username: String, password: String): Flow<UserEntity?>

    @Query("SELECT * FROM user_table WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM user_table WHERE id = :userId")
    fun getUserById(userId: Int): Flow<UserEntity>

    @Query("UPDATE user_table SET totalPoints = :points, currentLevel = :level WHERE id = :userId")
    suspend fun updateUserPoints(userId: Int, points: Int, level: String)

    // --- TRANSAKSI ---
    @Insert
    suspend fun insertTransaksi(transaksi: TransaksiEntity)

    @Query("SELECT * FROM transaksi_sampah WHERE userId = :userId ORDER BY date DESC")
    fun getTransaksiByUser(userId: Int): Flow<List<TransaksiEntity>>

    // FUNGSI BARU: HAPUS DATA
    @Delete
    suspend fun deleteTransaksi(transaksi: TransaksiEntity)
}