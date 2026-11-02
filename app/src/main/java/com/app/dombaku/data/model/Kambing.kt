package com.app.dombaku.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class Kambing(
    @DocumentId
    @get:Exclude @set:Exclude
    var id: String = "",

    @get:PropertyName("id_kambing") @set:PropertyName("id_kambing")
    var idKambing: String = "",

    @get:PropertyName("kode_tag") @set:PropertyName("kode_tag")
    var kodeTag: String = "",

    @get:PropertyName("nama") @set:PropertyName("nama")
    var nama: String = "",

    @get:PropertyName("pemilik_id") @set:PropertyName("pemilik_id")
    var pemilikId: String = "",

    @get:PropertyName("jenis_kelamin") @set:PropertyName("jenis_kelamin")
    var jenisKelamin: String = "JANTAN",

    @get:PropertyName("harga_beli") @set:PropertyName("harga_beli")
    var hargaBeli: Long = 0,

    @get:PropertyName("estimasi_harga_jual") @set:PropertyName("estimasi_harga_jual")
    var estimasiHargaJual: Long = 0,

    @get:PropertyName("status_kondisi") @set:PropertyName("status_kondisi")
    var statusKondisi: String = "SEHAT",

    @get:PropertyName("asal_kambing") @set:PropertyName("asal_kambing")
    var asalKambing: String = "",

    @get:PropertyName("status_kepemilikan") @set:PropertyName("status_kepemilikan")
    var statusKepemilikan: String = "MILIK_SENDIRI",

    @get:PropertyName("tanggal_masuk") @set:PropertyName("tanggal_masuk")
    var tanggalMasuk: String = "",

    @get:PropertyName("id_induk_betina") @set:PropertyName("id_induk_betina")
    var idIndukBetina: String? = null,

    @get:PropertyName("id_induk_jantan") @set:PropertyName("id_induk_jantan")
    var idIndukJantan: String? = null,

    @get:PropertyName("berat") @set:PropertyName("berat")
    var berat: Double? = null,

    @get:PropertyName("jenis") @set:PropertyName("jenis")
    var jenis: String? = null
) {
    constructor() : this("")
}
