package com.example.ecopoint_project.ui.ux.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecopoint_project.data.repository.EcoRepository

// Kelas boilerplate standar untuk membuat ViewModel dengan Repository
class ViewModelFactory(private val repository: EcoRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EcoViewModel::class.java)) {
            return EcoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}