package com.app.dombaku

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.dombaku.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test untuk mencoba login menggunakan Firebase Auth + Firestore.
 * Jalankan test ini pada Android Emulator atau Device yang terhubung.
 */
@RunWith(AndroidJUnit4::class)
class AuthRepositoryTest {

    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        authRepository = AuthRepository()
        // Pastikan sign out terlebih dahulu sebelum test dimulai
        authRepository.logout()
    }

    @Test
    fun testFirebaseLogin() = runBlocking {
        // Email ini disesuaikan dengan user yang terdaftar di Firebase Console Anda (ad8101058@gmail.com)
        val emailTest = "ad8101058@gmail.com"
        // Ganti dengan password yang Anda buat di Firebase Console untuk user tersebut
        val passwordTest = "123456" 

        println("Mencoba login ke Firebase dengan email: $emailTest")
        val result = authRepository.login(emailTest, passwordTest)

        if (result.isSuccess) {
            val user = result.getOrNull()
            assertNotNull("User data tidak boleh null jika login sukses", user)
            assertEquals("Email tidak cocok", emailTest, user?.email)
            println("=== TEST SUCCESS ===")
            println("Login berhasil!")
            println("UID: ${user?.uid}")
            println("Nama: ${user?.nama}")
            println("Role: ${user?.role}")
            println("Status: ${user?.status}")
        } else {
            val exception = result.exceptionOrNull()
            println("=== TEST ERROR/FAILED ===")
            println("Gagal login ke Firebase: ${exception?.message}")
            fail("Login gagal: ${exception?.message}")
        }
    }
}
