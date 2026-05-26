package com.app.dombaku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dombaku.data.model.Kambing
import com.app.dombaku.data.repository.KambingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DombaListViewModel(
    private val repository: KambingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DombaListUiState>(DombaListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        streamKambing()
    }

    private fun streamKambing() {
        viewModelScope.launch {
            repository.getAllKambing().collect { list ->
                _uiState.value = DombaListUiState.Success(list)
            }
        }
    }
}

sealed class DombaListUiState {
    object Loading : DombaListUiState()
    data class Success(val list: List<Kambing>) : DombaListUiState()
    data class Error(val message: String) : DombaListUiState()
}
