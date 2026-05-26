package com.app.dombaku.data.repository

import com.app.dombaku.data.model.Kambing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

class KambingRepository() {

    suspend fun refreshKambing() {
        // TODO: Refresh from Firebase
    }

    fun getAllKambing(): Flow<List<Kambing>> = flow {
        // TODO: get from Firebase
        emit(emptyList())
    }

    fun getKambingByOwner(ownerId: Int): Flow<List<Kambing>> = flow {
        // TODO: get from Firebase
        emit(emptyList())
    }

    suspend fun getKambingById(id: Int): Kambing? {
        // TODO: get from Firebase
        return null
    }

    suspend fun insertKambing(kambing: Kambing): Boolean {
        // TODO: insert to Firebase
        return false
    }

    suspend fun updateKambing(kambing: Kambing): Boolean {
        // TODO: update in Firebase
        return false
    }

    suspend fun deleteKambing(id: Int): Boolean {
        // TODO: delete from Firebase
        return false
    }
}
