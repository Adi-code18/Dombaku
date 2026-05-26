package com.app.dombaku.ui.theme.screens.pengurus

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.dombaku.data.model.Kambing
import com.app.dombaku.ui.theme.*
import com.app.dombaku.ui.viewmodel.AuthViewModel
import com.app.dombaku.ui.viewmodel.DombaListUiState
import com.app.dombaku.ui.viewmodel.DombaListViewModel
import com.app.dombaku.ui.viewmodel.KambingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DombaListScreen(
    navController: NavController,
    viewModel: DombaListViewModel,
    authViewModel: AuthViewModel? = null,
    kambingViewModel: KambingViewModel,
    ownerId: String? = null
) {
    var searchQuery      by remember { mutableStateOf("") }
    var filterStatus     by remember { mutableStateOf("SEMUA") }
    var filterKelamin    by remember { mutableStateOf("SEMUA") }

    val uiState by viewModel.uiState.collectAsState()
    var selectedKambing by remember { mutableStateOf<Kambing?>(null) }

    val statusOptions  = listOf("SEMUA", "sehat", "sakit", "mati", "terjual")
    val kelaminOptions = listOf("SEMUA", "JANTAN", "BETINA")

    Scaffold(
        floatingActionButton = {
            // Hanya tampilkan tombol tambah jika bukan investor (ownerId null)
            if (ownerId == null) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("tambah_domba") },
                    containerColor = GreenPrimary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Filled.Add, "Tambah") },
                    text = { Text("Tambah") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (ownerId == null) "Cari ID domba atau pemilik..." else "Cari ID domba...") },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = GreenPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Close, null, tint = TextSecondary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            // Filter chips - Status
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusOptions) { option ->
                    FilterChipItem(
                        label = option,
                        selected = filterStatus == option,
                        onClick = { filterStatus = option }
                    )
                }
            }

            // ... (rest of filtering UI)
            Spacer(Modifier.height(6.dp))

            // Filter chips - Kelamin
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(kelaminOptions) { option ->
                    val color = when (option) {
                        "JANTAN" -> JantanColor
                        "BETINA" -> BetinaColor
                        else -> GreenPrimary
                    }
                    FilterChipItem(
                        label = option,
                        selected = filterKelamin == option,
                        onClick = { filterKelamin = option },
                        color = color
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            when (uiState) {
                is DombaListUiState.Loading -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GreenPrimary)
                    }
                }
                is DombaListUiState.Error -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text((uiState as DombaListUiState.Error).message, color = Color.Red)
                    }
                }
                is DombaListUiState.Success -> {
                    val fullList = (uiState as DombaListUiState.Success).list
                    val filteredList = fullList.filter { kambing ->
                        val matchOwner = ownerId == null || kambing.pemilik_id == ownerId
                        val matchSearch = kambing.namatag.contains(searchQuery, ignoreCase = true) || 
                                          kambing.nama.contains(searchQuery, ignoreCase = true)
                        val matchStatus = filterStatus == "SEMUA" || kambing.status.equals(filterStatus, ignoreCase = true)
                        val matchKelamin = filterKelamin == "SEMUA" || kambing.jeniskelamin.equals(filterKelamin, ignoreCase = true)
                        matchOwner && matchSearch && matchStatus && matchKelamin
                    }

                    Column {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${filteredList.size} domba ditemukan",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredList, key = { it.id }) { kambing ->
                                DombaCard(kambing = kambing, onClick = { selectedKambing = kambing })
                            }
                            item { Spacer(Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }

    if (selectedKambing != null) {
        DombaDetailScreen(
            kambing = selectedKambing!!,
            onDismiss = { selectedKambing = null },
            viewModel = kambingViewModel,
            authViewModel = authViewModel
        )
    }
}

@Composable
fun FilterChipItem(
    label    : String,
    selected : Boolean,
    onClick  : () -> Unit,
    color    : Color = GreenPrimary
) {
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = { Text(label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal) },
        colors   = FilterChipDefaults.filterChipColors(
            selectedContainerColor  = color.copy(alpha = 0.15f),
            selectedLabelColor      = color,
            selectedLeadingIconColor= color
        ),
        border   = FilterChipDefaults.filterChipBorder(
            enabled             = true,
            selected            = selected,
            selectedBorderColor = color,
            borderColor         = BorderColor
        )
    )
}

@Composable
fun DombaCard(kambing: Kambing, onClick: () -> Unit) {
    val (statusColor, statusBg, statusLabel) = when (kambing.status.lowercase()) {
        "sehat"   -> Triple(StatusHidup,   StatusHidupBg,   "Sehat")
        "sakit"   -> Triple(StatusSakit,   StatusSakitBg,   "Sakit")
        "mati"    -> Triple(StatusMati,    StatusMatiBg,    "Mati")
        "terjual" -> Triple(StatusTerjual, StatusTerjualBg, "Terjual")
        else      -> Triple(TextSecondary, SurfaceVariant,  kambing.status)
    }

    val kelaminColor = if (kambing.jeniskelamin.lowercase() == "jantan") JantanColor else BetinaColor
    val kelaminBg    = if (kambing.jeniskelamin.lowercase() == "jantan") JantanBg else BetinaBg
    val kelaminLabel = if (kambing.jeniskelamin.lowercase() == "jantan") "Jantan" else "Betina"

    Card(
        onClick    = onClick,
        modifier   = Modifier.fillMaxWidth(),
        shape      = RoundedCornerShape(16.dp),
        colors     = CardDefaults.cardColors(containerColor = Color.White),
        elevation  = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(kelaminBg),
                contentAlignment = Alignment.Center
            ) {
                Text(if (kambing.jeniskelamin.lowercase() == "jantan") "🐏" else "🐑", fontSize = 24.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        kambing.nama,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary
                    )
                    if (kambing.id_induk_jantan != null || kambing.id_induk_betina != null) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GreenContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Anakan", fontSize = 9.sp, color = GreenPrimaryDark, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniTag(kelaminLabel, kelaminColor, kelaminBg)
                    if (kambing.berat != null) {
                        MiniTag("${kambing.berat} kg", TextSecondary, SurfaceVariant)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "ID: ${kambing.namatag}  •  ${kambing.jenis ?: "Tanpa Jenis"}",
                    fontSize = 11.sp,
                    color    = TextHint
                )
            }

            // Status badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusBg)
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    statusLabel,
                    fontSize   = 11.sp,
                    color      = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun MiniTag(text: String, textColor: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text, fontSize = 10.sp, color = textColor, fontWeight = FontWeight.Medium)
    }
}
