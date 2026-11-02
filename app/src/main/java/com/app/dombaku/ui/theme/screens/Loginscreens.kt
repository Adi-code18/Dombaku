package com.app.dombaku.ui.theme.screens.auth

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
import com.app.dombaku.ui.viewmodel.AuthUiState
import com.app.dombaku.ui.viewmodel.AuthViewModel
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {

    var email        by remember { mutableStateOf("") }
    var password     by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var emailError   by remember { mutableStateOf("") }
    var passError    by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                val user = (uiState as AuthUiState.Success).user
                if (user.role.equals("admin", ignoreCase = true) || user.role.equals("pengurus", ignoreCase = true)) {
                    navController.navigate("admin") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    navController.navigate("investor") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, (uiState as AuthUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Entry animation
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
                modifier            = Modifier
                    .align(Alignment.Center)
                    .padding(top = 40.dp),
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
                    "Goat Farm",
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
                Text(
                    "Sistem Peternakan Bagi Hasil",
                    fontSize = 15.sp,
                    color    = Color.White.copy(alpha = 0.75f)
                )
            }
        }

        // Card login
        AnimatedVisibility(
            visible = visible,
            enter   = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec  = tween(500)
            ) + fadeIn(tween(500))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 220.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape  = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
                    ) {
                        Text(
                            "Selamat Datang ",
                            style      = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary
                        )
                        Text(
                            "Masuk untuk melanjutkan",
                            style  = MaterialTheme.typography.bodyMedium,
                            color  = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                        )

                        // Email Field
                        OutlinedTextField(
                            value         = email,
                            onValueChange = {
                                email  = it
                                emailError = ""
                            },
                            label         = { Text("Email") },
                            leadingIcon   = {
                                Icon(
                                    Icons.Outlined.Email,
                                    contentDescription = null,
                                    tint = GreenPrimary
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError  = emailError.isNotEmpty(),
                            supportingText = if (emailError.isNotEmpty()) {
                                { Text(emailError, color = StatusMati) }
                            } else null,
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = OutlinedTextFieldDefaults.colors(
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
                            value         = password,
                            onValueChange = {
                                password  = it
                                passError = ""
                            },
                            label       = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = GreenPrimary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        if (showPassword) Icons.Filled.VisibilityOff
                                        else Icons.Filled.Visibility,
                                        contentDescription = if (showPassword) "Hide password" else "Show password",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = passError.isNotEmpty(),
                            supportingText = if (passError.isNotEmpty()) {
                                { Text(passError, color = StatusMati) }
                            } else null,
                            modifier   = Modifier.fillMaxWidth(),
                            shape      = RoundedCornerShape(14.dp),
                            colors     = OutlinedTextFieldDefaults.colors(
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

                        Spacer(Modifier.height(8.dp))

                        // Lupa password
                        TextButton(
                            onClick  = { /* TODO: Forgot password */ },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                "Lupa Password?",
                                color    = GreenPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Login Button
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
                                if (password.isBlank()) {
                                    passError = "Password tidak boleh kosong"
                                    valid = false
                                } else if (password.length < 6) {
                                    passError = "Password minimal 6 karakter"
                                    valid = false
                                }
                                if (valid) {
                                    viewModel.login(email, password)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape  = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary
                            ),
                            enabled = uiState !is AuthUiState.Loading
                        ) {
                            if (uiState is AuthUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color    = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(Modifier.width(10.dp))
                                Text("Memverifikasi...", color = Color.White)
                            } else {
                                Icon(
                                    Icons.Outlined.Login,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Masuk",
                                    color      = Color.White,
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(16.dp))

                        // Divider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
                            Text(
                                "  atau  ",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextHint
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
                        }

                        Spacer(Modifier.height(16.dp))

                        // Aktivasi Akun Button (untuk Pemilik baru)
                        OutlinedButton(
                            onClick = { navController.navigate("aktivasi") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, GreenPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = GreenPrimary
                            )
                        ) {
                            Icon(
                                Icons.Outlined.PersonAdd,
                                contentDescription = null,
                                tint = GreenPrimary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Aktivasi Akun Pemilik",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // Register Pengurus link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Daftar sebagai Pengurus?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            TextButton(onClick = { navController.navigate("register") }) {
                                Text(
                                    "Daftar",
                                    color = GreenPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Info role
                Spacer(Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape  = RoundedCornerShape(16.dp),
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
                            tint     = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "Cara masuk:",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "• Pengurus: Daftar akun sendiri lalu login\n• Pemilik: Hubungi Pengurus untuk didaftarkan,\n  lalu Aktivasi Akun di sini",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    "Goat Farm © 2025  •  Sistem Peternakan Digital",
                    fontSize  = 11.sp,
                    color     = TextHint,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
