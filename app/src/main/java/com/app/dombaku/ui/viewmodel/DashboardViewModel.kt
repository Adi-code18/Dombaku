package com.app.dombaku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dombaku.data.model.Kambing
import com.app.dombaku.data.model.TransaksiKambing
import com.app.dombaku.data.repository.KambingRepository
import com.app.dombaku.data.repository.TransaksiKambingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: KambingRepository,
    private val transaksiRepository: TransaksiKambingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            
            repository.refreshKambing()
            
            combine(
                repository.getAllKambing(),
                transaksiRepository.getAllTransaksi()
            ) { list, transaksi ->
                val stats = calculateStats(list, transaksi)
                DashboardUiState.Success(list, stats)
            }.catch { e ->
                _uiState.value = DashboardUiState.Error(e.message ?: "Unknown error")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun calculateStats(list: List<Kambing>, transaksi: List<TransaksiKambing>): DashboardStats {
        val totalProfitPengurus = transaksi.filter { it.jenis_transaksi == "JUAL" }.sumOf { it.pendapatan_pengurus }
        
        return DashboardStats(
            total = list.count { it.status != "mati" && it.status != "terjual" },
            jantan = list.count { it.status != "mati" && it.status != "terjual" && it.jenis_kelamin == "JANTAN" },
            betina = list.count { it.status != "mati" && it.status != "terjual" && it.jenis_kelamin == "BETINA" },
            anakan = list.count { it.status != "mati" && it.status != "terjual" && (it.id_induk_jantan != null || it.id_induk_betina != null) },
            terjual = list.count { it.status == "terjual" },
            totalProfit = totalProfitPengurus,
            totalAset = 0
        )
    }
}

data class DashboardStats(
    val total: Int = 0,
    val jantan: Int = 0,
    val betina: Int = 0,
    val anakan: Int = 0,
    val terjual: Int = 0,
    val totalProfit: Long = 0,
    val totalAset: Long = 0
)

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val list: List<Kambing>, val stats: DashboardStats) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}
