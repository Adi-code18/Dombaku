package com.app.dombaku.data.model

/**
 * Model User sesuai field di Firestore collection 'users'
 */
data class User(
    val uid: String = "",
    var nama: String = "",
    var email: String = "",
    var role: String = "pemilik",         // 'pengurus' | 'pemilik'
    var saldoprofit: Long = 0,            // Sesuai screenshot
    var status: String = "active",
    var noHp: String = "",
    var createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this(uid = "")
}

object RoleConstants {
    const val PENGURUS = "pengurus"
    const val PEMILIK  = "pemilik"
}
