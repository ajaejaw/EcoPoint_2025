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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EcoViewModel(private val repository: EcoRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    private val _loginState = MutableStateFlow<String>("")
    val loginState: StateFlow<String> = _loginState

    private val _historyList = MutableStateFlow<List<TransaksiEntity>>(emptyList())
    val historyList: StateFlow<List<TransaksiEntity>> = _historyList

    // --- STATISTIK MINGGUAN (BARU) ---
    private val _weeklyStats = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val weeklyStats: StateFlow<List<Pair<String, Double>>> = _weeklyStats

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
                calculateWeeklyStats(daftarSampah) // Hitung grafik saat data dimuat
            }
        }
    }

    // --- HITUNG DATA GRAFIK (BARU) ---
    private fun calculateWeeklyStats(list: List<TransaksiEntity>) {
        val statsMap = mutableMapOf<String, Double>()
        val dateFormat = SimpleDateFormat("EEE", Locale("id", "ID")) // Format Hari (Sen, Sel, dll)

        // 1. Buat wadah kosong untuk 7 hari terakhir (agar grafik tidak bolong)
        for (i in 6 downTo 0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val dayName = dateFormat.format(cal.time)
            statsMap[dayName] = 0.0
        }

        // 2. Isi dengan data transaksi yang ada (filter 7 hari terakhir)
        val oneWeekAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.timeInMillis

        list.filter { it.date >= oneWeekAgo }.forEach { item ->
            val dayName = dateFormat.format(Date(item.date))
            if (statsMap.containsKey(dayName)) {
                statsMap[dayName] = statsMap[dayName]!! + item.weightInKg
            }
        }

        _weeklyStats.value = statsMap.toList()
    }

    fun setorSampah(jenis: String, berat: Double) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val poinDidapat = (berat * 100).toInt()
            val transaksi = TransaksiEntity(
                userId = user.id, trashType = jenis, weightInKg = berat, earnedPoints = poinDidapat
            )
            repository.insertTransaksi(transaksi)

            val totalPoinBaru = user.totalPoints + poinDidapat
            repository.updateUserPoints(user.id, totalPoinBaru, hitungLevel(totalPoinBaru))
            _currentUser.value = repository.getUserById(user.id).first()
        }
    }

    fun deleteSampah(item: TransaksiEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.deleteTransaksi(item)
            val totalPoinBaru = (user.totalPoints - item.earnedPoints).coerceAtLeast(0)
            repository.updateUserPoints(user.id, totalPoinBaru, hitungLevel(totalPoinBaru))
            _currentUser.value = repository.getUserById(user.id).first()
            // Data grafik akan otomatis terupdate karena dipanggil di loadUserData -> collect
        }
    }
    
    fun updateSampah(updatedItem: TransaksiEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val originalItem = _historyList.value.find { it.id == updatedItem.id } ?: return@launch
            val newPoints = (updatedItem.weightInKg * 100).toInt()
            val pointDifference = newPoints - originalItem.earnedPoints
            val finalUpdatedItem = updatedItem.copy(earnedPoints = newPoints)
            repository.updateTransaksi(finalUpdatedItem)
            val newTotalUserPoints = (user.totalPoints + pointDifference).coerceAtLeast(0)
            repository.updateUserPoints(user.id, newTotalUserPoints, hitungLevel(newTotalUserPoints))
            _currentUser.value = repository.getUserById(user.id).first()
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