package com.app.dombaku.data.model

/**
 * Tipe kepemilikan untuk menentukan skema bagi hasil.
 */
enum class KepemilikanDomba {
    FULL_PEMILIK,   // Milik investor 100%, pengurus dapat upah/biaya saja
    BAGI_HASIL,     // Sistem parohan / bagi hasil keuntungan
    TITIPAN         // Titipan biasa
}
