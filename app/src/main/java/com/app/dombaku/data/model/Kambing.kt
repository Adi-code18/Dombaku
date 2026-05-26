package com.app.dombaku.data.model

import com.google.firebase.firestore.DocumentId

/**
 * Model Kambing sesuai field di Firestore collection 'kambings'
 */
data class Kambing(
    @DocumentId
    val id: String = "",              // Document ID Firestore
    val namatag: String = "",         // Sesuai screenshot (e.g. "KMB-002")
    val pemilik_id: String = "",      // Sesuai screenshot (UID Pemilik)
    val jeniskelamin: String = "jantan", // Sesuai screenshot (kecil semua)
    val harga_beli: Long = 0,         // Sesuai screenshot (Number)
    val kategori: String = "",        // Sesuai screenshot (e.g. "indukan")
    val status: String = "sehat",     // Sesuai screenshot
    
    // Field pendukung lainnya (pastikan ada di Firestore jika ingin digunakan)
    val nama: String = "",
    val berat: String? = null,
    val jenis: String? = null,
    val tgl_lahir: String? = null,
    val id_induk_jantan: String? = null,
    val id_induk_betina: String? = null
) {
    constructor() : this(id = "")
}
