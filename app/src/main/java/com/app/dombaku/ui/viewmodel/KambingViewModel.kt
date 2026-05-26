package com.app.dombaku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dombaku.data.model.Kambing
import com.app.dombaku.data.repository.KambingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class KambingViewModel(
    private val repository: KambingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<KambingUiState>(KambingUiState.Idle)
    val uiState = _uiState.asStateFlow()

    val allKambing: StateFlow<List<Kambing>> = repository.getAllKambing()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertKambing(kambing: Kambing) {
        viewModelScope.launch {
            _uiState.value = KambingUiState.Loading
            try {
                repository.insertKambing(kambing)
                _uiState.value = KambingUiState.Success("Data kambing berhasil disimpan")
            } catch (e: Exception) {
                _uiState.value = KambingUiState.Error(e.message ?: "Gagal simpan data")
            }
        }
    }

    fun deleteKambing(kambing: Kambing) {
        viewModelScope.launch {
            repository.deleteKambing(kambing.id)
        }
    }

    fun resetState() {
        _uiState.value = KambingUiState.Idle
    }
}

sealed class KambingUiState {
    object Idle : KambingUiState()
    object Loading : KambingUiState()
    data class Success(val message: String) : KambingUiState()
    data class Error(val message: String) : KambingUiState()
}
