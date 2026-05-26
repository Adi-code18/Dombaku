package com.app.dombaku.data.repository

import com.app.dombaku.data.model.TransaksiKambing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TransaksiKambingRepository() {
    
    fun getAllTransaksi(): Flow<List<TransaksiKambing>> = flow {
        // TODO: Get from Firebase
        emit(emptyList())
    }

    fun getTransaksiByKambing(idKambing: Int): Flow<List<TransaksiKambing>> = flow {
        // TODO: Get from Firebase
        emit(emptyList())
    }

    suspend fun insertTransaksi(transaksi: TransaksiKambing) {
        // TODO: Insert to Firebase
    }

    suspend fun deleteTransaksi(transaksi: TransaksiKambing) {
        // TODO: Delete from Firebase
    }
}
