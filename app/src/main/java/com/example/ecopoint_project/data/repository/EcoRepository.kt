package com.example.ecopoint_project.data.repository

import com.example.ecopoint_project.data.dao.EcoDao
import com.example.ecopoint_project.data.entity.TransaksiEntity
import com.example.ecopoint_project.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class EcoRepository(private val ecoDao: EcoDao) {

    // --- AUTH ---
    fun loginUser(username: String, password: String): Flow<UserEntity?> {
        return ecoDao.loginUser(username, password)
    }

    suspend fun getUserByUsername(username: String): UserEntity? {
        return ecoDao.getUserByUsername(username)
    }

    suspend fun registerUser(user: UserEntity) {
        ecoDao.insertUser(user)
    }

    fun getUserById(userId: Int): Flow<UserEntity> {
        return ecoDao.getUserById(userId)
    }

    suspend fun updateUserPoints(userId: Int, points: Int, level: String) {
        ecoDao.updateUserPoints(userId, points, level)
    }

    // --- TRANSAKSI ---
    suspend fun insertTransaksi(transaksi: TransaksiEntity) {
        ecoDao.insertTransaksi(transaksi)
    }

    fun getHistoryByUser(userId: Int): Flow<List<TransaksiEntity>> {
        return ecoDao.getTransaksiByUser(userId)
    }

    // FUNGSI BARU: PANGGIL DAO DELETE
    suspend fun deleteTransaksi(transaksi: TransaksiEntity) {
        ecoDao.deleteTransaksi(transaksi)
    }

    // FUNGSI BARU: PANGGIL DAO UPDATE
    suspend fun updateTransaksi(transaksi: TransaksiEntity) {
        ecoDao.updateTransaksi(transaksi)
    }
}