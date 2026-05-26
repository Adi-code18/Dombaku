package com.app.dombaku.ui.theme.screens.pengurus

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.dombaku.data.model.Kambing
import com.app.dombaku.ui.theme.*
import com.app.dombaku.ui.viewmodel.AuthUiState
import com.app.dombaku.ui.viewmodel.AuthViewModel
import com.app.dombaku.ui.viewmodel.KambingViewModel
import com.app.dombaku.ui.viewmodel.KambingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DombaDetailScreen(
    kambing: Kambing,
    onDismiss: () -> Unit,
    viewModel: KambingViewModel,
    authViewModel: AuthViewModel? = null
) {
    var catatanInput by remember { mutableStateOf("") } // Assuming catatan is handled elsewhere or to be added
    var selectedStatus by remember { mutableStateOf(kambing.status_kondisi) }
    
    // For selling logic
    var isSelling by remember { mutableStateOf(false) }
    var hargaJualInput by remember { mutableStateOf(kambing.estimasi_harga_jual.toString()) }
    
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is KambingUiState.Success) {
            snackbarHostState.showSnackbar((uiState as KambingUiState.Success).message)
            viewModel.resetState()
        } else if (uiState is KambingUiState.Error) {
            snackbarHostState.showSnackbar((uiState as KambingUiState.Error).message)
            viewModel.resetState()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = BackgroundLight
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            bottomBar = {
                Button(
                    onClick = {
                        // Update logic using KambingViewModel
                        // Since KambingViewModel doesn't have specific update functions yet, we update the whole entity
                        val updatedKambing = kambing.copy(
                            status_kondisi = selectedStatus,
                            estimasi_harga_jual = hargaJualInput.toLongOrNull() ?: kambing.estimasi_harga_jual
                        )
                        viewModel.insertKambing(updatedKambing) // Using insert as UPSERT if DAO supports it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState is KambingUiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (kambing.jenis_kelamin == "JANTAN") JantanBg else BetinaBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (kambing.jenis_kelamin == "JANTAN") "🐏" else "🐑", fontSize = 32.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(kambing.id_kambing, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Pemilik: ${kambing.id_pemilik}", fontSize = 14.sp, color = TextSecondary)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Detail Cards
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailInfoCard(Modifier.weight(1f), "Jenis Kelamin", if (kambing.jenis_kelamin == "JANTAN") "Jantan" else "Betina", Icons.Outlined.Transgender)
                    DetailInfoCard(Modifier.weight(1f), "Status Hak", kambing.status_kepemilikan.replace("_", " "), Icons.Outlined.Gavel)
                }
                
                Spacer(Modifier.height(12.dp))

                if (!kambing.id_induk.isNullOrBlank()) {
                    DetailInfoCard(Modifier.fillMaxWidth(), "ID Induk", kambing.id_induk ?: "-", Icons.Outlined.Pets)
                    Spacer(Modifier.height(12.dp))
                }

                // Status Selection
                Text("Status Kambing", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("HIDUP", "SAKIT", "MATI", "TERJUAL").forEach { status ->
                        val isSelected = selectedStatus == status
                        val color = when(status) {
                            "HIDUP" -> StatusHidup
                            "SAKIT" -> StatusSakit
                            "MATI" -> StatusMati
                            "TERJUAL" -> StatusTerjual
                            else -> TextSecondary
                        }
                        
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedStatus = status },
                            label = { Text(status, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.1f),
                                selectedLabelColor = color
                            )
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Edit Fields
                // Kambing model doesn't have beratKg field directly in this schema? 
                // Let's check the schema again. It has kode_tag, asal_kambing, tanggal_masuk, harga_beli, estimasi_harga_jual.
                
                OutlinedTextField(
                    value = kambing.kode_tag,
                    onValueChange = { },
                    label = { Text("Kode Tag") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Outlined.QrCode, null) }
                )

                Spacer(Modifier.height(16.dp))
                
                // Jual Logic
                if (selectedStatus == "TERJUAL" && kambing.status_kondisi != "TERJUAL") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = GreenContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Data Penjualan", fontWeight = FontWeight.Bold, color = GreenPrimaryDark)
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = hargaJualInput,
                                onValueChange = { hargaJualInput = it },
                                label = { Text("Estimasi Harga Jual (Rp)") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                prefix = { Text("Rp ") }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = catatanInput,
                    onValueChange = { catatanInput = it },
                    label = { Text("Catatan / Keterangan") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Outlined.Notes, null) }
                )
                
                Spacer(Modifier.height(100.dp)) // Padding for button
            }
        }
    }
}

@Composable
fun DetailInfoCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 11.sp, color = TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}
