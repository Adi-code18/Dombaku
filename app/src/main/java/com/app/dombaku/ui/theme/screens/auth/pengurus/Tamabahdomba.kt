package com.app.dombaku.ui.theme.screens.pengurus

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.dombaku.data.model.Kambing
import com.app.dombaku.ui.theme.*
import com.app.dombaku.ui.viewmodel.AuthUiState
import com.app.dombaku.ui.viewmodel.AuthViewModel
import com.app.dombaku.ui.viewmodel.KambingUiState
import com.app.dombaku.ui.viewmodel.KambingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahDombaScreen(
    navController: NavController,
    viewModel: KambingViewModel,
    authViewModel: AuthViewModel
) {

    // Form state
    var tipeEntri       by remember { mutableStateOf("BARU") }  // "BARU" | "ANAKAN"
    var idKambing       by remember { mutableStateOf("") }
    var kodeTag         by remember { mutableStateOf("") }
    var jenisKelamin    by remember { mutableStateOf("") }
    var hargaBeli       by remember { mutableStateOf("") }
    var tanggalMasuk    by remember { mutableStateOf("") }
    var idInduk         by remember { mutableStateOf("") }
    var idPemilik       by remember { mutableStateOf("") }
    var asalKambing     by remember { mutableStateOf("") }
    var statusKepemilikan by remember { mutableStateOf("MILIK_SENDIRI") }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is KambingUiState.Success -> {
                Toast.makeText(context, (uiState as KambingUiState.Success).message, Toast.LENGTH_SHORT).show()
                navController.popBackStack()
                viewModel.resetState()
            }
            is KambingUiState.Error -> {
                Toast.makeText(context, (uiState as KambingUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GreenPrimary)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.AddCircle, null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Tambah Kambing", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Catat kambing baru atau kelahiran anakan", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Tipe Entry Toggle
        SectionLabel("Jenis Entri")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TipeCard(
                label    = "🛒  Beli Baru",
                sublabel = "Kambing baru dari pembelian",
                selected = tipeEntri == "BARU",
                onClick  = { tipeEntri = "BARU" },
                modifier = Modifier.weight(1f)
            )
            TipeCard(
                label    = "🐣  Anakan",
                sublabel = "Lahir dari indukan",
                selected = tipeEntri == "ANAKAN",
                onClick  = { tipeEntri = "ANAKAN" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Form Fields
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                FormField(
                    label       = "ID Kambing *",
                    value       = idKambing,
                    onValueChange = { idKambing = it },
                    placeholder = "Contoh: KMB-001",
                    icon        = Icons.Outlined.Badge,
                    keyboardType = KeyboardType.Text
                )
                
                Spacer(Modifier.height(14.dp))

                FormField(
                    label       = "Kode Tag",
                    value       = kodeTag,
                    onValueChange = { kodeTag = it },
                    placeholder = "Scan atau ketik kode tag",
                    icon        = Icons.Outlined.QrCode,
                    keyboardType = KeyboardType.Text
                )

                Spacer(Modifier.height(14.dp))

                SectionLabel("Jenis Kelamin *")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GenderCard("JANTAN", "🐏", JantanColor, JantanBg, jenisKelamin == "JANTAN") {
                        jenisKelamin = "JANTAN"
                    }
                    GenderCard("BETINA", "🐑", BetinaColor, BetinaBg, jenisKelamin == "BETINA") {
                        jenisKelamin = "BETINA"
                    }
                }

                Spacer(Modifier.height(14.dp))

                FormField(
                    label       = "Username Pemilik *",
                    value       = idPemilik,
                    onValueChange = { idPemilik = it },
                    placeholder = "Contoh: budi123",
                    icon        = Icons.Outlined.Person,
                    keyboardType = KeyboardType.Text
                )

                Spacer(Modifier.height(14.dp))

                FormField(
                    label       = "Asal Kambing",
                    value       = asalKambing,
                    onValueChange = { asalKambing = it },
                    placeholder = "Contoh: Pasar Hewan",
                    icon        = Icons.Outlined.LocationOn,
                    keyboardType = KeyboardType.Text
                )

                AnimatedVisibility(visible = tipeEntri == "BARU") {
                    Column {
                        Spacer(Modifier.height(14.dp))
                        FormField(
                            label       = "Harga Beli *",
                            value       = hargaBeli,
                            onValueChange = { hargaBeli = it },
                            placeholder = "Contoh: 2500000",
                            icon        = Icons.Outlined.Payments,
                            keyboardType = KeyboardType.Number,
                            prefix      = "Rp"
                        )
                    }
                }

                AnimatedVisibility(visible = tipeEntri == "ANAKAN") {
                    Column {
                        Spacer(Modifier.height(14.dp))
                        FormField(
                            label       = "ID Induk *",
                            value       = idInduk,
                            onValueChange = { idInduk = it },
                            placeholder = "Contoh: KMB-002",
                            icon        = Icons.Outlined.FamilyRestroom,
                            keyboardType = KeyboardType.Text
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                FormField(
                    label       = "Tanggal Masuk / Lahir *",
                    value       = tanggalMasuk,
                    onValueChange = { tanggalMasuk = it },
                    placeholder = "YYYY-MM-DD",
                    icon        = Icons.Outlined.CalendarToday,
                    keyboardType = KeyboardType.Text
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Submit button
        Button(
            onClick = {
                if (idKambing.isBlank() || idPemilik.isBlank() || jenisKelamin.isBlank() || tanggalMasuk.isBlank()) {
                    Toast.makeText(context, "Mohon lengkapi data wajib", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                
                val kambing = Kambing(
                    idKambing = idKambing.trim(),
                    kodeTag = kodeTag.trim(),
                    pemilikId = idPemilik.trim(),
                    idIndukBetina = if (tipeEntri == "ANAKAN") idInduk.trim() else null,
                    jenisKelamin = jenisKelamin,
                    asalKambing = asalKambing.trim(),
                    statusKepemilikan = statusKepemilikan,
                    tanggalMasuk = tanggalMasuk.trim(),
                    hargaBeli = hargaBeli.toLongOrNull() ?: 0L,
                    statusKondisi = "SEHAT"
                )
                viewModel.insertKambing(kambing)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp) // Beri jarak bawah agar tidak tertutup navbar
                .height(54.dp),
            shape  = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            enabled = uiState !is KambingUiState.Loading
        ) {
            if (uiState is KambingUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.5.dp)
                Spacer(Modifier.width(8.dp))
                Text("Menyimpan...", color = Color.White)
            } else {
                Icon(Icons.Filled.Save, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Simpan Data Kambing", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text,
        fontSize   = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color      = TextSecondary,
        modifier   = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun TipeCard(label: String, sublabel: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Card(
        onClick   = onClick,
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (selected) GreenContainer else Color.White
        ),
        border    = BorderStroke(
            width = 2.dp,
            color = if (selected) GreenPrimary else BorderColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (selected) GreenPrimaryDark else TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(sublabel, fontSize = 11.sp, color = TextHint)
        }
    }
}

@Composable
fun GenderCard(label: String, emoji: String, color: Color, bgColor: Color, selected: Boolean, onClick: () -> Unit) {
    Card(
        onClick   = onClick,
        modifier  = Modifier.width(100.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = if (selected) bgColor else Color.White),
        border    = BorderStroke(2.dp, if (selected) color else BorderColor)
    ) {
        Column(
            modifier            = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (selected) color else TextSecondary)
        }
    }
}

@Composable
fun FormField(
    label        : String,
    value        : String,
    onValueChange: (String) -> Unit,
    placeholder  : String,
    icon         : androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType : KeyboardType,
    prefix       : String? = null
) {
    Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = { Text(placeholder, color = TextHint, fontSize = 13.sp) },
        leadingIcon   = { Icon(icon, null, tint = GreenPrimary) },
        prefix        = if (prefix != null) {{ Text(prefix, color = TextSecondary) }} else null,
        modifier      = Modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor    = GreenPrimary,
            unfocusedBorderColor  = BorderColor,
            focusedLabelColor     = GreenPrimary,
            cursorColor           = GreenPrimary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun InfoBagiAnakan(jumlah: Int) {
    val jatahPemilik  = jumlah / 2
    val jatahPengurus = jumlah / 2
    val sisaJoint     = jumlah % 2 != 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant)
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Info, null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Pembagian otomatis $jumlah anakan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GreenPrimaryDark)
            }
            Spacer(Modifier.height(6.dp))
            Text("• Pemilik: $jatahPemilik ekor", fontSize = 12.sp, color = TextPrimary)
            Text("• Pengurus: $jatahPengurus ekor", fontSize = 12.sp, color = TextPrimary)
            if (sisaJoint) {
                Text("• 1 ekor sisa → Status JOINT 50:50", fontSize = 12.sp, color = StatusSakit, fontWeight = FontWeight.Medium)
            }
        }
    }
}