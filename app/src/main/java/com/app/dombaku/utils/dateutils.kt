package com.app.dombaku.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * DateUtils
 *
 * Kumpulan fungsi utilitas untuk:
 *  1. Hitung umur domba dalam bulan/hari dari tanggal masuk
 *  2. Format tanggal untuk tampilan
 *  3. Parse string tanggal "YYYY-MM-DD"
 *  4. Generate label waktu relatif
 *
 * Menggunakan java.util.Calendar & SimpleDateFormat agar kompatibel dengan API 24+
 * tanpa perlu desugaring tambahan.
 */
object DateUtils {

    private val sdfDisplay = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    private val sdfInput   = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))

    // ─────────────────────────────────────────────────────────────────────────
    // HITUNG UMUR
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Hitung umur dalam bulan dari string tanggal "YYYY-MM-DD".
     */
    fun hitungUmurBulan(tanggalStr: String?): Int {
        if (tanggalStr.isNullOrBlank()) return 0
        return try {
            val date = sdfInput.parse(tanggalStr) ?: return 0
            hitungUmurBulanDariDate(date)
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Hitung umur dalam bulan dari timestamp Millis.
     */
    fun hitungUmurBulan(millis: Long?): Int {
        if (millis == null || millis == 0L) return 0
        return hitungUmurBulanDariDate(Date(millis))
    }

    /**
     * Helper hitung selisih bulan menggunakan Calendar (Safe for API 24).
     */
    fun hitungUmurBulanDariDate(date: Date?): Int {
        if (date == null) return 0
        val birth = Calendar.getInstance().apply { time = date }
        val today = Calendar.getInstance()
        
        var months = (today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)) * 12
        months += today.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
        
        // Jika hari ini belum mencapai hari kelahiran di bulan ini, kurangi 1
        if (today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH)) {
            months--
        }
        return if (months < 0) 0 else months
    }

    /**
     * Hitung umur dalam hari.
     */
    fun hitungUmurHari(tanggalStr: String?): Long {
        if (tanggalStr.isNullOrBlank()) return 0
        return try {
            val date = sdfInput.parse(tanggalStr) ?: return 0
            val diff = System.currentTimeMillis() - date.time
            diff / (1000 * 60 * 60 * 24)
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Tampilan umur yang manusiawi.
     */
    fun umurLabel(tanggalStr: String?): String {
        if (tanggalStr.isNullOrBlank()) return "Tidak diketahui"
        val bulan = hitungUmurBulan(tanggalStr)
        val hari = hitungUmurHari(tanggalStr)

        return when {
            bulan == 0 -> "$hari hari"
            bulan < 12 -> "$bulan bulan"
            else -> {
                val thn = bulan / 12
                val sisaBln = bulan % 12
                if (sisaBln == 0) "$thn thn" else "$thn thn $sisaBln bln"
            }
        }
    }

    /** Overload untuk Long millis */
    fun umurLabel(millis: Long?): String {
        if (millis == null || millis == 0L) return "Tidak diketahui"
        val date = Date(millis)
        val bulan = hitungUmurBulanDariDate(date)
        
        val diff = System.currentTimeMillis() - millis
        val hari = diff / (1000 * 60 * 60 * 24)

        return when {
            bulan == 0 -> "$hari hari"
            bulan < 12 -> "$bulan bulan"
            else -> {
                val thn = bulan / 12
                val sisaBln = bulan % 12
                if (sisaBln == 0) "$thn thn" else "$thn thn $sisaBln bln"
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FORMAT TANGGAL
    // ─────────────────────────────────────────────────────────────────────────

    fun formatDisplay(date: Date?): String = if (date == null) "-" else sdfDisplay.format(date)

    fun formatDisplay(millis: Long?): String {
        if (millis == null || millis == 0L) return "-"
        return sdfDisplay.format(Date(millis))
    }

    fun formatDisplay(tanggalStr: String?): String {
        if (tanggalStr.isNullOrBlank()) return "-"
        return try {
            val date = sdfInput.parse(tanggalStr) ?: return "-"
            sdfDisplay.format(date)
        } catch (e: Exception) {
            tanggalStr
        }
    }

    fun todayDisplay(): String = sdfDisplay.format(Date())

    fun todayIso(): String = sdfInput.format(Date())

    // ─────────────────────────────────────────────────────────────────────────
    // WAKTU RELATIF
    // ─────────────────────────────────────────────────────────────────────────

    fun waktuRelatif(timestampMs: Long): String {
        val diff    = System.currentTimeMillis() - timestampMs
        val detik   = diff / 1000
        val menit   = detik / 60
        val jam     = menit / 60
        val hari    = jam   / 24

        return when {
            detik < 60   -> "Baru saja"
            menit < 60   -> "$menit menit lalu"
            jam   < 24   -> "$jam jam lalu"
            hari  == 1L  -> "Kemarin"
            hari  < 30   -> "$hari hari lalu"
            hari  < 365  -> "${hari / 30} bulan lalu"
            else         -> "${hari / 365} tahun lalu"
        }
    }

    fun waktuRelatif(millis: Long?): String {
        if (millis == null || millis == 0L) return "-"
        return waktuRelatif(millis)
    }

    fun waktuRelatif(date: Date?): String {
        if (date == null) return "-"
        return waktuRelatif(date.time)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PARSE & VALIDATE
    // ─────────────────────────────────────────────────────────────────────────

    fun parseIso(tanggalStr: String): Date? {
        return try { sdfInput.parse(tanggalStr) } catch (e: Exception) { null }
    }

    fun isValidDateFormat(tanggalStr: String): Boolean {
        if (tanggalStr.length != 10) return false
        return try {
            sdfInput.parse(tanggalStr)
            true
        } catch (e: Exception) {
            false
        }
    }
}
