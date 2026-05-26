package com.app.dombaku

import android.app.Application
import com.app.dombaku.data.repository.AuthRepository
import com.app.dombaku.data.repository.KambingRepository
import com.app.dombaku.data.repository.KelahiranRepository
import com.app.dombaku.data.repository.TransaksiKambingRepository

class DombakuApp : Application() {
    
    val authRepository by lazy { AuthRepository() }

    val kambingRepository by lazy { KambingRepository() }
    val transaksiKambingRepository by lazy { TransaksiKambingRepository() }
    val kelahiranRepository by lazy { KelahiranRepository() }
}
