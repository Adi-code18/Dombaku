package com.app.dombaku.data.model

import java.util.Date

data class Kawin(
    val idKawin: String = "",
    val idInduk: String = "",
    val namaInduk: String = "",
    val idPejantan: String = "",
    val namaPejantan: String = "",
    val asalPejantan: String = "INTERNAL", // "INTERNAL", "EKSTERNAL"
    val tanggalKawin: String = "", // "YYYY-MM-DD"
    val estimasiLahir: String = "", // "YYYY-MM-DD"
    val tanggalMelahirkan: String? = null,
    val status: String = "DIJADWALKAN", // "DIJADWALKAN", "BUNTING", "MELAHIRKAN", "GAGAL"
    val catatan: String = "",
    val createdBy: String = "",
    val idPemilikInduk: String = "",
    val jumlahAnakan: Int = 0,
    val anakanHidup: Int = 0,
    val anakanMati: Int = 0
)

enum class StatusKawin {
    DIJADWALKAN,
    DIKONFIRMASI_BUNTING,
    MELAHIRKAN,
    GAGAL
}

enum class AsalPejantan {
    INTERNAL,
    EKSTERNAL
}

const val MASA_BUNTING_HARI = 150

data class HasilBagiAnakan(
    val jumlahTotal: Int,
    val jatahPemilik: Int,
    val jatahPengurus: Int,
    val adaAnakJoint: Boolean,
    val daftarKepemilikan: List<KepemilikanDomba>
)

data class HasilBagiHitung(
    val hargaModal: Long,
    val hargaJual: Long,
    val keuntunganBersih: Long,
    val bagianPemilik: Long,
    val bagianPengurus: Long,
    val statusKepemilikan: KepemilikanDomba
) {
    val isUntung: Boolean get() = keuntunganBersih > 0
    val isImpas: Boolean get() = keuntunganBersih == 0L
}
