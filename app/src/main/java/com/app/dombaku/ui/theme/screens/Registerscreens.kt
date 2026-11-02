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
import com.app.dombaku.ui.viewmodel.RegisterUiState
import com.app.dombaku.ui.viewmodel.AuthViewModel

/**
 * Register Screen — KHUSUS PENGURUS (self-registration).
 * Pengurus mendaftar sendiri dengan email & password, langsung aktif.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {

    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    var namaError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passError by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf("") }

    val uiState by viewModel.registerUiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is RegisterUiState.Success -> {
                Toast.makeText(context, "Registrasi Pengurus berhasil! Silakan login.", Toast.LENGTH_LONG).show()
                viewModel.resetState()
                navController.popBackStack()
            }
            is RegisterUiState.Error -> {
                Toast.makeText(context, (uiState as RegisterUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
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
            // Decorative circles
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
                    "Daftar Pengurus",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Buat akun pengurus baru",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }

        // Card register
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
                        Text(
                            "Daftar Akun Pengurus",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            "Lengkapi data di bawah ini",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                        )

                        // Nama Field
                        OutlinedTextField(
                            value = nama,
                            onValueChange = {
                                nama = it
                                namaError = ""
                            },
                            label = { Text("Nama Lengkap") },
                            leadingIcon = {
                                Icon(Icons.Outlined.PersonOutline, contentDescription = null, tint = GreenPrimary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            isError = namaError.isNotEmpty(),
                            supportingText = if (namaError.isNotEmpty()) { { Text(namaError, color = StatusMati) } } else null,
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

                        // Email Field
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
                            supportingText = if (emailError.isNotEmpty()) { { Text(emailError, color = StatusMati) } } else null,
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

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passError = ""
                            },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Lock, contentDescription = null, tint = GreenPrimary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = if (showPassword) "Hide password" else "Show password",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = passError.isNotEmpty(),
                            supportingText = if (passError.isNotEmpty()) { { Text(passError, color = StatusMati) } } else null,
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

                        // Confirm Password Field
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
                            supportingText = if (confirmError.isNotEmpty()) { { Text(confirmError, color = StatusMati) } } else null,
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

                        // Register Button
                        Button(
                            onClick = {
                                var valid = true
                                if (nama.isBlank()) {
                                    namaError = "Nama tidak boleh kosong"
                                    valid = false
                                }
                                if (email.isBlank()) {
                                    emailError = "Email tidak boleh kosong"
                                    valid = false
                                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    emailError = "Format email tidak valid"
                                    valid = false
                                }
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
                                    viewModel.registerPengurus(nama, email, password)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            enabled = uiState !is RegisterUiState.Loading
                        ) {
                            if (uiState is RegisterUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(Modifier.width(10.dp))
                                Text("Mendaftar...", color = Color.White)
                            } else {
                                Icon(Icons.Outlined.AppRegistration, contentDescription = null, tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("Daftar sebagai Pengurus", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Info card
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    border = BorderStroke(1.dp, GreenContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Halaman ini khusus untuk Pengurus.\nUntuk Pemilik, silakan hubungi Pengurus\nagar didaftarkan terlebih dahulu.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
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
