package com.app.dombaku.data.model


/**
 * Model Kelahiran sesuai tabel 'kelahiran' di MySQL.
 */
data class Kelahiran(
    val idKelahiran  : String = "",
    val idInduk      : String = "",
    val tanggalLahir : String = "", // YYYY-MM-DD
    val jumlahAnak   : Int = 0,
    val keterangan    : String = ""
) {
    constructor() : this(idKelahiran = "")
}
