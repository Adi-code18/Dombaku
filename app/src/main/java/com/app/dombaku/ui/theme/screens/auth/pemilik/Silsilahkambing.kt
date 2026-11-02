package com.app.dombaku.ui.theme.screens.pemilik

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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.dombaku.ui.theme.*
import com.app.dombaku.ui.viewmodel.SilsilahViewModel
import com.app.dombaku.ui.viewmodel.SilsilahUiState
import com.app.dombaku.ui.viewmodel.SilsilahNodeData
import com.app.dombaku.data.model.Kambing
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SilsilahScreen(
    navController: NavController,
    viewModel: SilsilahViewModel
) {
    var searchId by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundLight)
    ) {
        // Search
        OutlinedTextField(
            value         = searchId,
            onValueChange = { 
                searchId = it
                it.toIntOrNull()?.let { id ->
//                    viewModel.loadSilsilah(indukbetina_id)
                }
            },
            placeholder   = { Text("Cari ID domba untuk melihat silsilah...") },
            leadingIcon   = { Icon(Icons.Outlined.Search, null, tint = GreenPrimary) },
            trailingIcon  = {
                if (searchId.isNotEmpty()) IconButton({ 
                    searchId = "" 
                    viewModel.resetState()
                }) {
                    Icon(Icons.Filled.Close, null, tint = TextSecondary)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = GreenPrimary,
                unfocusedBorderColor    = BorderColor,
                focusedContainerColor   = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        // Legend
        Row(
            modifier              = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendItem("Sehat", StatusHidup)
            LegendItem("Sakit", StatusSakit)
            LegendItem("Terjual", StatusTerjual)
            LegendItem("Mati", StatusMati)
        }

        Spacer(Modifier.height(12.dp))

        when (uiState) {
            is SilsilahUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            }
            is SilsilahUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text((uiState as SilsilahUiState.Error).message, color = Color.Red)
                }
            }
            is SilsilahUiState.Success -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(rememberScrollState())
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    SilsilahNodeView(node = (uiState as SilsilahUiState.Success).root, level = 0)
                }
            }
            is SilsilahUiState.Idle -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.AccountTree, null, modifier = Modifier.size(64.dp), tint = TextHint)
                        Spacer(Modifier.height(8.dp))
                        Text("Masukkan ID Domba untuk melihat silsilah", color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun SilsilahNodeView(node: SilsilahNodeData, level: Int) {
    val domba = node.kambing
    val (statusColor, statusBg, statusLabel) = when (domba.statusKondisi.lowercase()) {
        "sehat"   -> Triple(StatusHidup,   StatusHidupBg,   "Sehat")
        "sakit"   -> Triple(StatusSakit,   StatusSakitBg,   "Sakit")
        "mati"    -> Triple(StatusMati,    StatusMatiBg,    "Mati")
        "terjual" -> Triple(StatusTerjual, StatusTerjualBg, "Terjual")
        else      -> Triple(TextSecondary, SurfaceVariant,  domba.statusKondisi)
    }

    val isExpanded = remember { mutableStateOf(true) }

    Column {
        Row(verticalAlignment = Alignment.Top) {
            // Vertical connector line
            if (level > 0) {
                val lineColor = DividerColor
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(28.dp)
                        .drawBehind {
                            drawLine(
                                color       = lineColor,
                                start       = Offset(0f, 0f),
                                end         = Offset(0f, size.height / 2),
                                strokeWidth = 2f,
                                pathEffect  = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
                            )
                            drawLine(
                                color       = lineColor,
                                start       = Offset(0f, size.height / 2),
                                end         = Offset(size.width, size.height / 2),
                                strokeWidth = 2f,
                                pathEffect  = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
                            )
                        }
                )
            }

            // Node card
            Card(
                onClick   = { if (node.children.isNotEmpty()) isExpanded.value = !isExpanded.value },
                modifier  = Modifier.width(170.dp),
                shape     = RoundedCornerShape(14.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                border    = BorderStroke(1.5.dp, statusColor.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (domba.jenisKelamin.uppercase() == "JANTAN") "🐏" else "🐑",
                            fontSize = 20.sp
                        )
                        if (node.children.isNotEmpty()) {
                            Icon(
                                if (isExpanded.value) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                null, tint = statusColor, modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(domba.nama, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        "ID: ${domba.idKambing.ifEmpty { domba.id }} • ${if (domba.jenisKelamin.uppercase() == "JANTAN") "Jantan" else "Betina"}",
                        fontSize = 10.sp, color = TextSecondary
                    )
                    Spacer(Modifier.height(6.dp))
                    
                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(statusLabel, fontSize = 9.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
                    }

                    if (node.children.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${node.children.size} anakan",
                            fontSize = 9.sp,
                            color    = TextHint
                        )
                    }
                }
            }
        }

        // Children
        AnimatedVisibility(visible = isExpanded.value) {
            if (node.children.isNotEmpty()) {
                Column(modifier = Modifier.padding(start = 24.dp)) {
                    node.children.forEach { child ->
                        Spacer(Modifier.height(8.dp))
                        SilsilahNodeView(
                            node  = child,
                            level = level + 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, color = TextSecondary)
    }
}
