package com.app.dombaku.data.repository

import com.app.dombaku.data.model.Kelahiran
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class KelahiranRepository() {
    fun getKelahiranByInduk(idInduk: String): Flow<List<Kelahiran>> = flow {
        // TODO: Get from Firebase
        emit(emptyList())
    }

    suspend fun insertKelahiran(kelahiran: Kelahiran) {
        // TODO: Insert to Firebase
    }

    suspend fun deleteKelahiran(kelahiran: Kelahiran) {
        // TODO: Delete from Firebase
    }
}
