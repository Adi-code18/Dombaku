package com.app.dombaku.data.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

/**
 * Model User sesuai field di Firestore collection 'users'
 */
@IgnoreExtraProperties
data class User(
    @get:PropertyName("uid") @set:PropertyName("uid")
    var uid: String = "",
    
    @get:PropertyName("nama") @set:PropertyName("nama")
    var nama: String = "",
    
    @get:PropertyName("email") @set:PropertyName("email")
    var email: String = "",
    
    @get:PropertyName("role") @set:PropertyName("role")
    var role: String = "pemilik",
    
    @get:PropertyName("saldoProfit") @set:PropertyName("saldoProfit")
    var saldoProfit: Long = 0,
    
    @get:PropertyName("status") @set:PropertyName("status")
    var status: String = "active",
    
    @get:PropertyName("noHp") @set:PropertyName("noHp")
    var noHp: String = "",
    
    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this(uid = "")
}

object RoleConstants {
    const val PENGURUS = "pengurus"
    const val PEMILIK  = "pemilik"
}

object StatusConstants {
    const val ACTIVE = "active"
    const val PENDING = "pending"
}
