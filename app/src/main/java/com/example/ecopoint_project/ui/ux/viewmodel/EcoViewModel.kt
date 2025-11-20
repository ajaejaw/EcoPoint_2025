package com.example.ecopoint_project.ui.ux.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecopoint_project.data.entity.TransaksiEntity
import com.example.ecopoint_project.data.entity.UserEntity
import com.example.ecopoint_project.data.repository.EcoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EcoViewModel(private val repository: EcoRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    private val _loginState = MutableStateFlow<String>("")
    val loginState: StateFlow<String> = _loginState

    private val _historyList = MutableStateFlow<List<TransaksiEntity>>(emptyList())
    val historyList: StateFlow<List<TransaksiEntity>> = _historyList

    // --- LOGIN ---
    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _historyList.value = emptyList()
            _currentUser.value = null

            val user = repository.loginUser(username, pass).first()

            if (user != null) {
                _currentUser.value = user
                _loginState.value = "Success"
                loadUserData(user.id)
            } else {
                _loginState.value = "Failed"
            }
        }
    }

    // --- REGISTER ---
    fun register(name: String, username: String, pass: String) {
        viewModelScope.launch {
            val existingUser = repository.getUserByUsername(username)
            if (existingUser == null) {
                val newUser = UserEntity(
                    name = name, username = username, password = pass,
                    totalPoints = 0, currentLevel = "Warga Baru"
                )
                repository.registerUser(newUser)
                _loginState.value = "RegisterSuccess"
            } else {
                _loginState.value = "UsernameTaken"
            }
        }
    }

    // --- LOGOUT ---
    fun logout() {
        _currentUser.value = null
        _historyList.value = emptyList()
        _loginState.value = ""
    }

    fun resetLoginState() { _loginState.value = "" }

    private fun loadUserData(userId: Int) {
        viewModelScope.launch {
            repository.getHistoryByUser(userId).collect { daftarSampah ->
                _historyList.value = daftarSampah
            }
        }
    }

    // --- SETOR SAMPAH ---
    fun setorSampah(jenis: String, berat: Double) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val poinDidapat = (berat * 100).toInt()

            val transaksi = TransaksiEntity(
                userId = user.id, trashType = jenis, weightInKg = berat, earnedPoints = poinDidapat
            )
            repository.insertTransaksi(transaksi)

            val totalPoinBaru = user.totalPoints + poinDidapat
            val levelBaru = hitungLevel(totalPoinBaru)

            repository.updateUserPoints(user.id, totalPoinBaru, levelBaru)
            _currentUser.value = repository.getUserById(user.id).first()
        }
    }

    // --- HAPUS SAMPAH (BARU) ---
    fun deleteSampah(item: TransaksiEntity) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            // 1. Hapus dari DB
            repository.deleteTransaksi(item)

            // 2. Kurangi Poin (Jangan sampai minus)
            val poinDikurangi = item.earnedPoints
            val totalPoinBaru = (user.totalPoints - poinDikurangi).coerceAtLeast(0)

            // 3. Cek Level (Siapa tahu turun level)
            val levelBaru = hitungLevel(totalPoinBaru)

            // 4. Update User
            repository.updateUserPoints(user.id, totalPoinBaru, levelBaru)

            // 5. Refresh UI
            _currentUser.value = repository.getUserById(user.id).first()
            loadUserData(user.id) // Refresh list riwayat
        }
    }

    private fun hitungLevel(poin: Int): String {
        return when {
            poin >= 5000 -> "Sultan Sampah"
            poin >= 1000 -> "Juragan Loak"
            else -> "Warga Baru"
        }
    }
}