package com.app.dombaku.data.repository

import android.util.Log
import com.app.dombaku.data.model.Kambing
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class KambingRepository {

    private val firestore = FirebaseFirestore.getInstance()
    
    // Pastikan koleksi ini namanya "kambing" di Firebase Console kamu
    private val kambingCollection = firestore.collection("kambing")

    fun getAllKambing(): Flow<List<Kambing>> = callbackFlow {
        val listener = kambingCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirestoreDebug", "Listen failed: ${error.message}")
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.toObjects(Kambing::class.java)
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun insertKambing(kambing: Kambing) {
        try {
            val docId = kambing.idKambing.trim()
            
            Log.d("FirestoreDebug", "Mencoba simpan kambing: $docId")

            if (docId.isNotEmpty()) {
                // Cara 1: Gunakan ID manual (ID Kambing)
                kambingCollection.document(docId).set(kambing).await()
                Log.d("FirestoreDebug", "BERHASIL SIMPAN dengan ID: $docId")
            } else {
                // Cara 2: ID otomatis jika input kosong
                val ref = kambingCollection.add(kambing).await()
                Log.d("FirestoreDebug", "BERHASIL SIMPAN dengan Auto-ID: ${ref.id}")
            }
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "GAGAL SIMPAN: ${e.message}", e)
            throw e
        }
    }

    suspend fun deleteKambing(id: String) {
        try {
            kambingCollection.document(id).delete().await()
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Hapus gagal: ${e.message}")
        }
    }
}
