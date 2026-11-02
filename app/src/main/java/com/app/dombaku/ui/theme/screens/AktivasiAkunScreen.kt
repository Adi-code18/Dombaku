package com.app.dombaku.ui.theme.screens.auth

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.dombaku.R
import com.app.dombaku.ui.theme.*
import com.app.dombaku.ui.viewmodel.AktivasiUiState
import com.app.dombaku.ui.viewmodel.AuthViewModel

/**
 * Aktivasi Akun Screen — KHUSUS PEMILIK
 *
 * Alur:
 * Step 1: Pemilik memasukkan email -> app cek di Firestore apakah sudah di-pre-register
 * Step 2: Jika ditemukan, tampilkan info nama + minta bikin password
 * Step 3: createUserWithEmailAndPassword -> akun aktif -> navigasi ke login
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AktivasiAkunScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf("") }
    var passError by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf("") }

    // Step tracking: false = cek email, true = bikin password
    var isEmailVerified by remember { mutableStateOf(false) }
    var foundUserName by remember { mutableStateOf("") }

    val uiState by viewModel.aktivasiUiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is AktivasiUiState.EmailFound -> {
                val user = (uiState as AktivasiUiState.EmailFound).user
                foundUserName = user.nama
                isEmailVerified = true
            }
            is AktivasiUiState.Activated -> {
                Toast.makeText(context, "Akun berhasil diaktivasi! Silakan login.", Toast.LENGTH_LONG).show()
                viewModel.resetAktivasiState()
                navController.navigate("login") {
                    popUpTo("aktivasi") { inclusive = true }
                }
            }
            is AktivasiUiState.Error -> {
                Toast.makeText(context, (uiState as AktivasiUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetAktivasiState()
            }
            else -> {}
        }
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top gradient header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GreenPrimaryDark, GreenPrimary)
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 50.dp, y = (-30).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f))
            )
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-30).dp, y = 40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 40.dp, start = 16.dp),
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kambinglogo),
                        contentDescription = "Logo Domba",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Aktivasi Akun",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Untuk Pemilik yang sudah didaftarkan",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }

        // Card
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(500)
            ) + fadeIn(tween(500))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 200.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Step indicator
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    border = BorderStroke(1.dp, GreenContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Step 1
                        StepIndicator(
                            stepNumber = 1,
                            label = "Cek Email",
                            isActive = !isEmailVerified,
                            isDone = isEmailVerified
                        )

                        Icon(
                            Icons.Filled.ArrowForward,
                            contentDescription = null,
                            tint = if (isEmailVerified) GreenPrimary else TextHint,
                            modifier = Modifier.size(20.dp)
                        )

                        // Step 2
                        StepIndicator(
                            stepNumber = 2,
                            label = "Buat Password",
                            isActive = isEmailVerified,
                            isDone = false
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Main card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
                    ) {
                        if (!isEmailVerified) {
                            // ===== STEP 1: CEK EMAIL =====
                            Text(
                                "Verifikasi Email",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "Masukkan email yang sudah didaftarkan oleh Pengurus",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    emailError = ""
                                },
                                label = { Text("Email") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Email, contentDescription = null, tint = GreenPrimary)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                isError = emailError.isNotEmpty(),
                                supportingText = if (emailError.isNotEmpty()) {
                                    { Text(emailError, color = StatusMati) }
                                } else null,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor     = TextPrimary,
                                    unfocusedTextColor   = TextPrimary,
                                    focusedBorderColor   = GreenPrimary,
                                    focusedLabelColor    = GreenPrimary,
                                    unfocusedLabelColor  = TextSecondary,
                                    cursorColor          = GreenPrimary,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLeadingIconColor   = GreenPrimary,
                                    unfocusedLeadingIconColor = TextSecondary,
                                    focusedTrailingIconColor  = GreenPrimary,
                                    unfocusedTrailingIconColor = TextSecondary,
                                    errorTextColor            = StatusMati,
                                    errorBorderColor          = StatusMati,
                                    errorLabelColor           = StatusMati,
                                    errorLeadingIconColor     = StatusMati,
                                    errorTrailingIconColor    = StatusMati
                                ),
                                singleLine = true
                            )

                            Spacer(Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    var valid = true
                                    if (email.isBlank()) {
                                        emailError = "Email tidak boleh kosong"
                                        valid = false
                                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                        emailError = "Format email tidak valid"
                                        valid = false
                                    }
                                    if (valid) {
                                        viewModel.checkPreRegistered(email)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                enabled = uiState !is AktivasiUiState.Loading
                            ) {
                                if (uiState is AktivasiUiState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.5.dp
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text("Memeriksa...", color = Color.White)
                                } else {
                                    Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.White)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Cek Email",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                        } else {
                            // ===== STEP 2: BIKIN PASSWORD =====

                            // Welcome card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                                border = BorderStroke(1.dp, GreenContainer)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = StatusHidup,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Email ditemukan!",
                                            fontWeight = FontWeight.Bold,
                                            color = StatusHidup,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            "Halo, $foundUserName 👋",
                                            color = TextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            Text(
                                "Buat Password",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "Buat password untuk akun Anda",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                            )

                            // Password
                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    passError = ""
                                },
                                label = { Text("Password Baru") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = GreenPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showPassword = !showPassword }) {
                                        Icon(
                                            if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = null,
                                            tint = TextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                isError = passError.isNotEmpty(),
                                supportingText = if (passError.isNotEmpty()) {
                                    { Text(passError, color = StatusMati) }
                                } else null,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor     = TextPrimary,
                                    unfocusedTextColor   = TextPrimary,
                                    focusedBorderColor   = GreenPrimary,
                                    focusedLabelColor    = GreenPrimary,
                                    unfocusedLabelColor  = TextSecondary,
                                    cursorColor          = GreenPrimary,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLeadingIconColor   = GreenPrimary,
                                    unfocusedLeadingIconColor = TextSecondary,
                                    focusedTrailingIconColor  = GreenPrimary,
                                    unfocusedTrailingIconColor = TextSecondary,
                                    errorTextColor            = StatusMati,
                                    errorBorderColor          = StatusMati,
                                    errorLabelColor           = StatusMati,
                                    errorLeadingIconColor     = StatusMati,
                                    errorTrailingIconColor    = StatusMati
                                ),
                                singleLine = true
                            )

                            Spacer(Modifier.height(12.dp))

                            // Confirm Password
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    confirmError = ""
                                },
                                label = { Text("Konfirmasi Password") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = GreenPrimary)
                                },
                                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                isError = confirmError.isNotEmpty(),
                                supportingText = if (confirmError.isNotEmpty()) {
                                    { Text(confirmError, color = StatusMati) }
                                } else null,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor     = TextPrimary,
                                    unfocusedTextColor   = TextPrimary,
                                    focusedBorderColor   = GreenPrimary,
                                    focusedLabelColor    = GreenPrimary,
                                    unfocusedLabelColor  = TextSecondary,
                                    cursorColor          = GreenPrimary,
                                    unfocusedBorderColor = BorderColor,
                                    focusedLeadingIconColor   = GreenPrimary,
                                    unfocusedLeadingIconColor = TextSecondary,
                                    focusedTrailingIconColor  = GreenPrimary,
                                    unfocusedTrailingIconColor = TextSecondary,
                                    errorTextColor            = StatusMati,
                                    errorBorderColor          = StatusMati,
                                    errorLabelColor           = StatusMati,
                                    errorLeadingIconColor     = StatusMati,
                                    errorTrailingIconColor    = StatusMati
                                ),
                                singleLine = true
                            )

                            Spacer(Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    var valid = true
                                    if (password.isBlank()) {
                                        passError = "Password tidak boleh kosong"
                                        valid = false
                                    } else if (password.length < 6) {
                                        passError = "Password minimal 6 karakter"
                                        valid = false
                                    }
                                    if (confirmPassword != password) {
                                        confirmError = "Password tidak cocok"
                                        valid = false
                                    }
                                    if (valid) {
                                        viewModel.activateAccount(email, password)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                enabled = uiState !is AktivasiUiState.Loading
                            ) {
                                if (uiState is AktivasiUiState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.5.dp
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text("Mengaktifkan...", color = Color.White)
                                } else {
                                    Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = Color.White)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Aktivasi Akun",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // Back to step 1
                            TextButton(
                                onClick = {
                                    isEmailVerified = false
                                    password = ""
                                    confirmPassword = ""
                                    viewModel.resetAktivasiState()
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    "← Gunakan email lain",
                                    color = GreenPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
                Text(
                    "Goat Farm © 2025  •  Sistem Peternakan Digital",
                    fontSize = 11.sp,
                    color = TextHint,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StepIndicator(
    stepNumber: Int,
    label: String,
    isActive: Boolean,
    isDone: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone -> StatusHidup
                        isActive -> GreenPrimary
                        else -> TextHint.copy(alpha = 0.3f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    "$stepNumber",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            fontSize = 11.sp,
            color = if (isActive || isDone) TextPrimary else TextHint,
            fontWeight = if (isActive || isDone) FontWeight.Medium else FontWeight.Normal
        )
    }
}
