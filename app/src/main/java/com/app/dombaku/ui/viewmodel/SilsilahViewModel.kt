package com.app.dombaku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dombaku.data.model.Kambing
import com.app.dombaku.data.repository.KambingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SilsilahViewModel(
    private val repository: KambingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SilsilahUiState>(SilsilahUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun loadSilsilah(idInduk: String) {
        viewModelScope.launch {
            _uiState.value = SilsilahUiState.Loading
            repository.getAllKambing().collect { allKambing ->
                val rootDomba = allKambing.find { it.id == idInduk || it.namatag == idInduk }
                if (rootDomba != null) {
                    val tree = buildTree(rootDomba, allKambing)
                    _uiState.value = SilsilahUiState.Success(tree)
                } else {
                    _uiState.value = SilsilahUiState.Error("Domba dengan ID $idInduk tidak ditemukan")
                }
            }
        }
    }

    private fun buildTree(current: Kambing, all: List<Kambing>): SilsilahNodeData {
        val children = all.filter { it.id_induk_jantan == current.id || it.id_induk_betina == current.id }
        return SilsilahNodeData(
            kambing = current,
            children = children.map { buildTree(it, all) }
        )
    }

    fun resetState() {
        _uiState.value = SilsilahUiState.Idle
    }
}

data class SilsilahNodeData(
    val kambing: Kambing,
    val children: List<SilsilahNodeData> = emptyList()
)

sealed class SilsilahUiState {
    object Idle : SilsilahUiState()
    object Loading : SilsilahUiState()
    data class Success(val root: SilsilahNodeData) : SilsilahUiState()
    data class Error(val message: String) : SilsilahUiState()
}
