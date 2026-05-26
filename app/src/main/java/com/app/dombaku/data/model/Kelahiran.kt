package com.app.dombaku.data.model


/**
 * Model Kelahiran sesuai tabel 'kelahiran' di MySQL.
 */
data class Kelahiran(
    val id_kelahiran  : Int = 0,
    val id_induk      : String = "",
    val tanggal_lahir : String = "", // YYYY-MM-DD
    val jumlah_anak   : Int = 0,
    val keterangan    : String = ""
)
