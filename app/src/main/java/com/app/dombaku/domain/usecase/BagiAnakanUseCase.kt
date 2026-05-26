package com.app.dombaku.domain.usecase

import com.app.dombaku.data.model.HasilBagiAnakan
import com.app.dombaku.data.model.KepemilikanDomba

class BagiAnakanUseCase {
    /**
     * Menghitung pembagian anakan (lambs) antara pemilik dan pengurus.
     * Menggunakan sistem parohan (50:50).
     * Jika jumlah ganjil, 1 ekor menjadi milik bersama (joint).
     * 
     * @param jumlah Total anakan yang lahir dan hidup
     * @return Result berisi HasilBagiAnakan
     */
    fun hitungPembagian(jumlah: Int): Result<HasilBagiAnakan> {
        if (jumlah < 0) {
            return Result.failure(IllegalArgumentException("Jumlah anakan tidak boleh negatif"))
        }
        
        val jatahPemilik = jumlah / 2
        val jatahPengurus = jumlah / 2
        val adaAnakJoint = jumlah % 2 != 0
        
        val daftarKepemilikan = mutableListOf<KepemilikanDomba>()
        
        // Tambahkan jatah pemilik
        repeat(jatahPemilik) {
            daftarKepemilikan.add(KepemilikanDomba.FULL_PEMILIK)
        }
        
        // Tambahkan jatah pengurus
        // (Dalam konteks aplikasi ini, anakan yang sudah dibagi ke pengurus 
        // mungkin dianggap miliknya sendiri atau tetap dalam sistem bagi hasil baru)
        repeat(jatahPengurus) {
            daftarKepemilikan.add(KepemilikanDomba.FULL_PEMILIK) 
        }
        
        // Tambahkan anak joint jika ada
        if (adaAnakJoint) {
            daftarKepemilikan.add(KepemilikanDomba.BAGI_HASIL)
        }
        
        val hasil = HasilBagiAnakan(
            jumlahTotal = jumlah,
            jatahPemilik = jatahPemilik,
            jatahPengurus = jatahPengurus,
            adaAnakJoint = adaAnakJoint,
            daftarKepemilikan = daftarKepemilikan
        )
        
        return Result.success(hasil)
    }
}
