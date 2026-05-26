package com.app.dombaku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.dombaku.data.repository.*

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val kambingRepository: KambingRepository,
    private val transaksiKambingRepository: TransaksiKambingRepository,
    private val kelahiranRepository: KelahiranRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(KambingViewModel::class.java) -> {
                KambingViewModel(kambingRepository) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(kambingRepository, transaksiKambingRepository) as T
            }
            modelClass.isAssignableFrom(DombaListViewModel::class.java) -> {
                DombaListViewModel(kambingRepository) as T
            }
            modelClass.isAssignableFrom(SilsilahViewModel::class.java) -> {
                SilsilahViewModel(kambingRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
