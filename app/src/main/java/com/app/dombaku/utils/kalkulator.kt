package com.app.dombaku.utils

import com.app.dombaku.data.model.HasilBagiAnakan
import com.app.dombaku.data.model.HasilBagiHitung
import com.app.dombaku.data.model.KepemilikanDomba
import com.app.dombaku.domain.usecase.BagiAnakanUseCase
import com.app.dombaku.domain.usecase.HitungBagiHasilUseCase
import java.text.NumberFormat
import java.util.Locale

/**
 * Kalkulator
 *
 * Facade / helper object yang menggabungkan semua kalkulasi bisnis
 * ke dalam satu titik akses. Dipakai langsung di screen Composable
 * tanpa perlu instantiate use case secara terpisah.
 *
 * Juga berisi fungsi format angka untuk UI.
 */
object Kalkulator {

    private val bagiHasilUseCase = HitungBagiHasilUseCase()
    private val bagiAnakanUseCase = BagiAnakanUseCase()
    private val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    // ─────────────────────────────────────────────────────────────────────────
    // BAGI HASIL PENJUALAN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Hitung bagi hasil penjualan domba.
     *
     * @param hargaModal    Harga beli / modal awal (0 jika anakan)
     * @param hargaJual     Harga jual sekarang
     * @param kepemilikan   Tipe kepemilikan domba
     * @return [HasilBagiHitung] atau null jika input tidak valid
     */
    fun hitungBagiHasil(
        hargaModal  : Long,
        hargaJual   : Long,
        kepemilikan : KepemilikanDomba = KepemilikanDomba.FULL_PEMILIK
    ): HasilBagiHitung? {
        if (hargaJual <= 0) return null
        return try {
            bagiHasilUseCase.hitung(hargaModal, hargaJual, kepemilikan)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Versi lebih simpel: langsung return map string untuk tampil di UI.
     * Berguna di kalkulator interaktif (real-time saat user ketik harga).
     */
    fun hitungBagiHasilLabel(
        hargaModal  : Long,
        hargaJual   : Long,
        kepemilikan : KepemilikanDomba = KepemilikanDomba.FULL_PEMILIK
    ): Map<String, String> {
        val hasil = hitungBagiHasil(hargaModal, hargaJual, kepemilikan)
            ?: return mapOf(
                "keuntungan"   to "-",
                "pemilik"      to "-",
                "pengurus"     to "-",
                "status"       to "Input tidak valid"
            )

        return mapOf(
            "keuntungan"  to formatRupiah(hasil.keuntunganBersih),
            "pemilik"     to formatRupiah(hasil.bagianPemilik),
            "pengurus"    to formatRupiah(hasil.bagianPengurus),
            "status"      to when {
                hasil.isUntung -> "Untung ${formatRupiah(hasil.keuntunganBersih)}"
                hasil.isImpas  -> "Impas (modal kembali)"
                else           -> "Rugi ${formatRupiah(Math.abs(hasil.keuntunganBersih))}"
            }
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BAGI ANAKAN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Hitung pembagian anakan berdasarkan jumlah.
     */
    fun hitungBagiAnakan(jumlah: Int): HasilBagiAnakan? {
        if (jumlah <= 0) return null
        return bagiAnakanUseCase.hitungPembagian(jumlah).getOrNull()
    }

    /**
     * Deskripsi singkat pembagian anakan untuk ditampilkan di UI.
     * Contoh: "Pemilik: 1 | Pengurus: 1 | Bersama: 1"
     */
    fun labelBagiAnakan(jumlah: Int): String {
        val hasil = hitungBagiAnakan(jumlah) ?: return "-"
        return buildString {
            if (hasil.jatahPemilik > 0)  append("Pemilik: ${hasil.jatahPemilik}")
            if (hasil.jatahPengurus > 0) {
                if (length > 0) append("  |  ")
                append("Pengurus: ${hasil.jatahPengurus}")
            }
            if (hasil.adaAnakJoint) {
                if (length > 0) append("  |  ")
                append("Bersama (50:50): 1")
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ESTIMASI NILAI TERNAK
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Estimasi harga jual berdasarkan berat.
     * Default: Rp 80.000/kg (bisa disesuaikan harga pasar)
     */
    fun estimasiHarga(beratKg: Double, hargaPerKg: Long = 80_000L): Long =
        (beratKg * hargaPerKg).toLong()

    fun estimasiHargaLabel(beratKg: Double, hargaPerKg: Long = 80_000L): String =
        formatRupiah(estimasiHarga(beratKg, hargaPerKg))

    // ─────────────────────────────────────────────────────────────────────────
    // FORMAT RUPIAH
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Format nominal ke Rupiah: 5000000 → "Rp 5.000.000"
     */
    fun formatRupiah(nominal: Long): String =
        "Rp ${"%,d".format(nominal).replace(",", ".")}"

    fun formatRupiah(nominal: Double): String =
        formatRupiah(nominal.toLong())

    /**
     * Format ke notasi singkat: 5000000 → "5,0 jt" | 500000 → "500 rb"
     */
    fun formatSingkat(nominal: Long): String = when {
        nominal >= 1_000_000_000 -> "${"%.1f".format(nominal / 1_000_000_000.0)} M"
        nominal >= 1_000_000     -> "${"%.1f".format(nominal / 1_000_000.0)} jt"
        nominal >= 1_000         -> "${"%.0f".format(nominal / 1_000.0)} rb"
        else                     -> nominal.toString()
    }

    /**
     * Parse string input Rupiah → Long.
     * Contoh: "4.000.000" atau "4000000" → 4000000L
     */
    fun parseRupiah(input: String): Long {
        return input
            .replace("Rp", "")
            .replace(".", "")
            .replace(",", "")
            .trim()
            .toLongOrNull() ?: 0L
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VALIDASI INPUT
    // ─────────────────────────────────────────────────────────────────────────

    fun isValidNominal(input: String): Boolean =
        parseRupiah(input) > 0

    fun isHargaJualValid(hargaModal: Long, hargaJual: Long): ValidationResult {
        return when {
            hargaJual <= 0           -> ValidationResult.Error("Harga jual harus lebih dari 0")
            hargaJual < hargaModal   -> ValidationResult.Warning(
                "Harga jual (${formatRupiah(hargaJual)}) lebih rendah dari modal " +
                        "(${formatRupiah(hargaModal)}). Pemilik rugi ${formatRupiah(hargaModal - hargaJual)}."
            )
            else                     -> ValidationResult.Ok
        }
    }

    sealed class ValidationResult {
        object Ok                           : ValidationResult()
        data class Warning(val msg: String) : ValidationResult()
        data class Error  (val msg: String) : ValidationResult()
    }
}