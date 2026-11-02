package com.app.dombaku.data.model


/**
 * Model Transaksi sesuai tabel 'transaksi_kambing' di MySQL.
 */
data class TransaksiKambing(
    val idTransaksi: String = "",
    val idKambing: String = "",
    val jenisTransaksi: String = "", // 'BELI', 'JUAL', 'BIAYA'
    val tanggalTransaksi: String = "", // YYYY-MM-DD
    val hargaTotal: Long = 0L,
    val pendapatanPengurus: Long = 0L,
    val pendapatanPenitip: Long = 0L,
    val keterangan: String = ""
) {
    constructor() : this(idTransaksi = "")
}
