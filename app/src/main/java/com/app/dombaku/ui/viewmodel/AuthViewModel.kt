package com.app.dombaku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dombaku.data.model.User
import com.app.dombaku.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _registerUiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerUiState = _registerUiState.asStateFlow()

    private val _aktivasiUiState = MutableStateFlow<AktivasiUiState>(AktivasiUiState.Idle)
    val aktivasiUiState = _aktivasiUiState.asStateFlow()

    private val _pemilikListState = MutableStateFlow<PemilikListState>(PemilikListState.Idle)
    val pemilikListState = _pemilikListState.asStateFlow()

    // ==========================================
    // LOGIN (email + password)
    // ==========================================
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.login(email, password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Terjadi kesalahan")
                }
        }
    }

    // ==========================================
    // REGISTER PENGURUS (self-register, langsung aktif)
    // ==========================================
    fun registerPengurus(nama: String, email: String, password: String) {
        viewModelScope.launch {
            _registerUiState.value = RegisterUiState.Loading
            repository.registerPengurus(nama, email, password)
                .onSuccess {
                    _registerUiState.value = RegisterUiState.Success
                }
                .onFailure { error ->
                    _registerUiState.value = RegisterUiState.Error(error.message ?: "Registrasi gagal")
                }
        }
    }

    // ==========================================
    // PRE-REGISTER PEMILIK (dilakukan oleh Pengurus)
    // ==========================================
    fun preRegisterPemilik(nama: String, email: String, noHp: String) {
        viewModelScope.launch {
            _registerUiState.value = RegisterUiState.Loading
            repository.preRegisterPemilik(nama, email, noHp)
                .onSuccess {
                    _registerUiState.value = RegisterUiState.Success
                    // Refresh list pemilik setelah berhasil daftar
                    loadPemilikList()
                }
                .onFailure { error ->
                    _registerUiState.value = RegisterUiState.Error(error.message ?: "Gagal mendaftarkan pemilik")
                }
        }
    }

    // ==========================================
    // CEK EMAIL PRE-REGISTER (Pemilik cek apakah sudah didaftarkan)
    // ==========================================
    fun checkPreRegistered(email: String) {
        viewModelScope.launch {
            _aktivasiUiState.value = AktivasiUiState.Loading
            repository.checkPreRegistered(email)
                .onSuccess { user ->
                    _aktivasiUiState.value = AktivasiUiState.EmailFound(user)
                }
                .onFailure { error ->
                    _aktivasiUiState.value = AktivasiUiState.Error(error.message ?: "Email tidak ditemukan")
                }
        }
    }

    // ==========================================
    // AKTIVASI AKUN PEMILIK (bikin password sendiri)
    // ==========================================
    fun activateAccount(email: String, password: String) {
        viewModelScope.launch {
            _aktivasiUiState.value = AktivasiUiState.Loading
            repository.activateAccount(email, password)
                .onSuccess { user ->
                    _aktivasiUiState.value = AktivasiUiState.Activated(user)
                }
                .onFailure { error ->
                    _aktivasiUiState.value = AktivasiUiState.Error(error.message ?: "Aktivasi gagal")
                }
        }
    }

    // ==========================================
    // LOAD DAFTAR PEMILIK (untuk Pengurus)
    // ==========================================
    fun loadPemilikList() {
        viewModelScope.launch {
            _pemilikListState.value = PemilikListState.Loading
            repository.getPemilikList()
                .onSuccess { list ->
                    _pemilikListState.value = PemilikListState.Success(list)
                }
                .onFailure { error ->
                    _pemilikListState.value = PemilikListState.Error(error.message ?: "Gagal mengambil data")
                }
        }
    }

    // ==========================================
    // UPDATE PROFILE
    // ==========================================
    fun updateProfile(user: User) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.updateProfile(user)
                .onSuccess { updatedUser ->
                    _uiState.value = AuthUiState.Success(updatedUser)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Gagal memperbarui profil")
                }
        }
    }

    // ==========================================
    // LOGOUT
    // ==========================================
    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState.Idle
    }

    // ==========================================
    // RESET STATES
    // ==========================================
    fun resetState() {
        _uiState.value = AuthUiState.Idle
        _registerUiState.value = RegisterUiState.Idle
    }

    fun resetAktivasiState() {
        _aktivasiUiState.value = AktivasiUiState.Idle
    }
}

// ==========================================
// UI STATES
// ==========================================

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

sealed class AktivasiUiState {
    object Idle : AktivasiUiState()
    object Loading : AktivasiUiState()
    data class EmailFound(val user: User) : AktivasiUiState()
    data class Activated(val user: User) : AktivasiUiState()
    data class Error(val message: String) : AktivasiUiState()
}

sealed class PemilikListState {
    object Idle : PemilikListState()
    object Loading : PemilikListState()
    data class Success(val list: List<User>) : PemilikListState()
    data class Error(val message: String) : PemilikListState()
}
