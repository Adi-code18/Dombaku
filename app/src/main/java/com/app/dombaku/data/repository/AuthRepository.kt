package com.app.dombaku.data.repository

import com.app.dombaku.data.model.RoleConstants
import com.app.dombaku.data.model.StatusConstants
import com.app.dombaku.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository untuk autentikasi menggunakan Firebase Auth + Firestore.
 *
 * Alur:
 * - Pengurus: login biasa (email+password) -> sudah punya akun Firebase Auth
 * - Pengurus: pre-register pemilik -> simpan data ke Firestore (status: pending)
 * - Pemilik: aktivasi akun -> cek email di Firestore -> createUserWithEmailAndPassword
 * - Pemilik: login biasa setelah aktivasi
 */
class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    // ==========================================
    // LOGIN (untuk Pengurus & Pemilik yang sudah aktif)
    // ==========================================
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID tidak ditemukan")

            // Ambil data user dari Firestore
            val doc = usersCollection.document(uid).get().await()
            if (doc.exists()) {
                val user = doc.toObject(User::class.java)?.copy(uid = uid)
                    ?: throw Exception("Data user tidak valid")

                if (user.status != StatusConstants.ACTIVE) {
                    auth.signOut()
                    throw Exception("Akun belum diaktivasi")
                }

                Result.success(user)
            } else {
                auth.signOut()
                Result.failure(Exception("Data profil tidak ditemukan di database"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Login gagal: ${e.message}"))
        }
    }

    // ==========================================
    // REGISTER PENGURUS (self-registration, langsung aktif)
    // ==========================================
    suspend fun registerPengurus(nama: String, email: String, password: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Gagal mendapatkan User ID")

            val user = User(
                uid = uid,
                nama = nama,
                email = email,
                role = RoleConstants.PENGURUS,
                status = StatusConstants.ACTIVE,
                createdAt = System.currentTimeMillis()
            )

            // Gunakan set() pada document(uid) agar ID di Firestore sama dengan UID di Authentication
            try {
                usersCollection.document(uid).set(user).await()
                Result.success(Unit)
            } catch (e: Exception) {
                // Jika Firestore gagal, kita beri peringatan khusus (biasanya masalah Rules)
                Result.failure(Exception("User terdaftar di Auth, tapi gagal simpan profil: ${e.localizedMessage}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Registrasi gagal: ${e.message}"))
        }
    }

    // ==========================================
    // PRE-REGISTER PEMILIK (dilakukan oleh Pengurus)
    // Simpan data ke Firestore saja, belum bikin akun Firebase Auth
    // ==========================================
    suspend fun preRegisterPemilik(nama: String, email: String, noHp: String): Result<Unit> {
        return try {
            // Cek apakah email sudah pernah didaftarkan
            val existing = usersCollection
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!existing.isEmpty) {
                return Result.failure(Exception("Email '$email' sudah terdaftar"))
            }

            val user = User(
                uid = "",  // Belum punya UID, karena belum dibuatkan akun Auth
                nama = nama,
                email = email,
                noHp = noHp,
                role = "pemilik",
                status = StatusConstants.PENDING,
                createdAt = System.currentTimeMillis()
            )

            // Simpan dengan auto-generated document ID
            usersCollection.add(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mendaftarkan pemilik: ${e.message}"))
        }
    }

    // ==========================================
    // CEK APAKAH EMAIL SUDAH DI-PRE-REGISTER oleh Pengurus
    // ==========================================
    suspend fun checkPreRegistered(email: String): Result<User> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("email", email)
                .whereEqualTo("status", StatusConstants.PENDING)
                .get()
                .await()

            if (snapshot.isEmpty) {
                Result.failure(Exception("Email belum didaftarkan oleh Pengurus.\nHubungi Pengurus Anda untuk mendaftarkan akun."))
            } else {
                val doc = snapshot.documents.first()
                val user = doc.toObject(User::class.java)
                    ?: throw Exception("Data tidak valid")
                Result.success(user.copy(uid = doc.id)) // pakai document ID sementara
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal memeriksa data: ${e.message}"))
        }
    }

    // ==========================================
    // AKTIVASI AKUN PEMILIK
    // Pemilik bikin password sendiri -> createUserWithEmailAndPassword
    // ==========================================
    suspend fun activateAccount(email: String, password: String): Result<User> {
        return try {
            // 1. Cek dulu apakah email memang sudah di-pre-register
            val snapshot = usersCollection
                .whereEqualTo("email", email)
                .whereEqualTo("status", StatusConstants.PENDING)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return Result.failure(Exception("Email belum didaftarkan oleh Pengurus"))
            }

            val oldDoc = snapshot.documents.first()
            val userData = oldDoc.toObject(User::class.java)
                ?: throw Exception("Data pemilik tidak valid")

            // 2. Buat akun Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID tidak ditemukan")

            // 3. Buat dokumen baru dengan UID sebagai document ID, status = active
            val activatedUser = userData.copy(
                uid = uid,
                status = StatusConstants.ACTIVE,
                createdAt = System.currentTimeMillis()
            )

            usersCollection.document(uid).set(activatedUser).await()

            // 4. Hapus dokumen pre-register yang lama (auto-generated ID)
            oldDoc.reference.delete().await()

            Result.success(activatedUser)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal aktivasi akun: ${e.message}"))
        }
    }

    // ==========================================
    // GET DAFTAR PEMILIK (untuk Pengurus lihat siapa saja yang sudah didaftarkan)
    // ==========================================
    suspend fun getPemilikList(): Result<List<User>> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("role", "pemilik")
                .get()
                .await()

            val list = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(uid = doc.id)
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengambil data pemilik: ${e.message}"))
        }
    }

    // ==========================================
    // UPDATE PROFILE
    // ==========================================
    suspend fun updateProfile(user: User): Result<User> {
        return try {
            if (user.uid.isBlank()) throw Exception("UID tidak valid")
            usersCollection.document(user.uid).set(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal memperbarui profil: ${e.message}"))
        }
    }

    // ==========================================
    // LOGOUT
    // ==========================================
    fun logout() {
        auth.signOut()
    }

    // ==========================================
    // GET CURRENT USER (cek session)
    // ==========================================
    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return try {
            val doc = usersCollection.document(firebaseUser.uid).get().await()
            doc.toObject(User::class.java)?.copy(uid = firebaseUser.uid)
        } catch (e: Exception) {
            null
        }
    }
}
