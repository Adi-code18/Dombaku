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
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.dombaku.ui.theme.*
import com.app.dombaku.ui.viewmodel.DashboardUiState
import com.app.dombaku.ui.viewmodel.DashboardViewModel
import com.app.dombaku.utils.Kalkulator

// ---- Data UI helpers ----
data class StatCard(
    val label     : String,
    val value     : String,
    val icon      : ImageVector,
    val color     : Color,
    val bgColor   : Color,
    val trend     : String = ""
)

data class AktivitasItem(
    val title   : String,
    val waktu   : String,
    val jenis   : String,   // "masuk" | "jual" | "lahir" | "mati"
    val dombaId : String
)

@Composable
fun DashboardScreen(navController: NavController, viewModel: DashboardViewModel = viewModel()) {

    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is DashboardUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        }
        is DashboardUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text((uiState as DashboardUiState.Error).message, color = Color.Red)
            }
        }
        is DashboardUiState.Success -> {
            val data = (uiState as DashboardUiState.Success)
            val stats = listOf(
                StatCard("Total Domba",   "${data.stats.total}",  Icons.Filled.Pets,         GreenPrimary,   GreenContainer,  "aktif"),
                StatCard("Domba Jantan",  "${data.stats.jantan}", Icons.Filled.Male,         JantanColor,    JantanBg,        ""),
                StatCard("Domba Betina",  "${data.stats.betina}", Icons.Filled.Female,       BetinaColor,    BetinaBg,        ""),
                StatCard("Anakan Baru",   "${data.stats.anakan}", Icons.Filled.ChildCare,    GreenSecondary, GreenContainer,  "semua"),
                StatCard("Terjual",       "${data.stats.terjual}",Icons.Filled.Sell,         StatusTerjual,  StatusTerjualBg, ""),
                StatCard("Total Profit",  Kalkulator.formatRupiah(data.stats.totalProfit), Icons.Filled.AccountBalance, Color(0xFF00897B), Color(0xFFE0F2F1), "est"),
            )

            val aktivitas = data.list.take(5).map {
                AktivitasItem(
                    title = "Kambing ${it.id_kambing} ${if (it.id_induk != null) "Lahir" else "Masuk"}",
                    waktu = it.tanggal_masuk.ifBlank { "Baru" },
                    jenis = if (it.id_induk != null) "lahir" else "masuk",
                    dombaId = it.id_kambing
                )
            }

            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TotalAsetCard(data.stats.total, data.stats.totalAset)
                }

                item {
                    Text(
                        "Statistik Kandang",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary
                    )
                    Spacer(Modifier.height(10.dp))
                    val rows = stats.chunked(2)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        rows.forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                row.forEach { stat ->
                                    StatCardItem(stat = stat, modifier = Modifier.weight(1f))
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Aktivitas Terbaru",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary
                    )
                    Spacer(Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            aktivitas.forEachIndexed { index, item ->
                                AktivitasRow(item = item)
                                if (index < aktivitas.lastIndex) {
                                    HorizontalDivider(
                                        color    = DividerColor,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
fun TotalAsetCard(total: Int, totalAset: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(GreenPrimaryDark, GreenPrimary)
                )
            )
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.07f))
        )

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.AccountBalance,
                    contentDescription = null,
                    tint     = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Total Nilai Aset (Estimasi)", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                Kalkulator.formatRupiah(totalAset),
                fontSize   = 30.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "$total ekor aktif",
                fontSize = 12.sp,
                color    = Color.White.copy(alpha = 0.65f)
            )
        }
    }
}

@Composable
fun StatCardItem(stat: StatCard, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(stat.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    stat.icon,
                    contentDescription = null,
                    tint     = stat.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                stat.value,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
            Text(
                stat.label,
                fontSize = 12.sp,
                color    = TextSecondary
            )
        }
    }
}

@Composable
fun AktivitasRow(item: AktivitasItem) {
    val (dotColor, dotBg, icon) = when (item.jenis) {
        "lahir" -> Triple(GreenPrimary,   GreenContainer,   Icons.Filled.ChildCare)
        "jual"  -> Triple(StatusTerjual,  StatusTerjualBg,  Icons.Filled.Sell)
        "mati"  -> Triple(StatusMati,     StatusMatiBg,     Icons.Filled.HeartBroken)
        "sakit" -> Triple(StatusSakit,    StatusSakitBg,    Icons.Filled.LocalHospital)
        else    -> Triple(GreenPrimary,  GreenContainer,   Icons.Filled.Pets)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(dotBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = dotColor, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(item.dombaId, fontSize = 11.sp, color = GreenPrimary)
        }
        Text(item.waktu, fontSize = 11.sp, color = TextHint)
    }
}
