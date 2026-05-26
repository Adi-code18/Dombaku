package com.app.dombaku.domain.usecase

import com.app.dombaku.data.model.HasilBagiHitung
import com.app.dombaku.data.model.KepemilikanDomba

class HitungBagiHasilUseCase {
    /**
     * Menghitung bagi hasil keuntungan penjualan ternak.
     * 
     * @param hargaModal Harga beli atau modal awal
     * @param hargaJual Harga jual ternak
     * @param kepemilikan Skema kepemilikan
     * @return Hasil kalkulasi bagi hasil
     */
    fun hitung(
        hargaModal: Long,
        hargaJual: Long,
        kepemilikan: KepemilikanDomba
    ): HasilBagiHitung {
        val keuntunganBersih = hargaJual - hargaModal
        
        var bagianPemilik = 0L
        var bagianPengurus = 0L
        
        when (kepemilikan) {
            KepemilikanDomba.BAGI_HASIL -> {
                if (keuntunganBersih > 0) {
                    // Sistem parohan: Untung dibagi 2
                    bagianPemilik = keuntunganBersih / 2
                    bagianPengurus = keuntunganBersih / 2
                } else {
                    // Jika rugi, modal pemilik berkurang (pengurus tidak dapat bagi hasil)
                    bagianPemilik = keuntunganBersih
                    bagianPengurus = 0L
                }
            }
            KepemilikanDomba.FULL_PEMILIK, KepemilikanDomba.TITIPAN -> {
                // Seluruh keuntungan/kerugian milik investor/pemilik
                bagianPemilik = keuntunganBersih
                bagianPengurus = 0L
            }
        }
        
        return HasilBagiHitung(
            hargaModal = hargaModal,
            hargaJual = hargaJual,
            keuntunganBersih = keuntunganBersih,
            bagianPemilik = bagianPemilik,
            bagianPengurus = bagianPengurus,
            statusKepemilikan = kepemilikan
        )
    }
}
