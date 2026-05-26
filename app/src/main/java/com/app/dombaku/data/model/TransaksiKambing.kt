package com.app.dombaku.data.model


/**
 * Model Transaksi sesuai tabel 'transaksi_kambing' di MySQL.
 */
data class TransaksiKambing(
    val id_transaksi: Int = 0,
    val id_kambing: Int = 0,
    val jenis_transaksi: String = "", // 'BELI', 'JUAL', 'BIAYA'
    val tanggal_transaksi: String = "", // YYYY-MM-DD
    val harga_total: Long = 0L,
    val pendapatan_pengurus: Long = 0L,
    val pendapatan_penitip: Long = 0L,
    val keterangan: String = ""
)
