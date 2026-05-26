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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.dombaku.ui.theme.*

data class TransaksiItem(
    val idTrx        : String,
    val idDomba      : String,
    val hargaModal   : Long,
    val hargaJual    : Long,
    val keuntungan   : Long,
    val bagianPemilik: Long,
    val bagianPengurus: Long,
    val tanggal      : String,
    val pemilik      : String
)

val dummyTransaksi = listOf(
    TransaksiItem("TRX-001","DMB-004", 4_000_000, 6_000_000, 2_000_000, 5_000_000, 1_000_000, "12 Jan 2025", "Pak Budi"),
    TransaksiItem("TRX-002","DMB-012", 3_500_000, 5_500_000, 2_000_000, 4_500_000, 1_000_000, "5 Feb 2025",  "Bu Sari"),
    TransaksiItem("TRX-003","DMB-018", 5_000_000, 7_200_000, 2_200_000, 6_100_000, 1_100_000, "1 Mar 2025",  "Pak Budi"),
    TransaksiItem("TRX-004","DMB-025", 4_200_000, 4_200_000, 0,         4_200_000, 0,          "20 Mar 2025", "Bu Sari"),
    TransaksiItem("TRX-005","DMB-031", 3_800_000, 5_900_000, 2_100_000, 4_850_000, 1_050_000, "8 Apr 2025",  "Pak Budi"),
)

@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun LaporanScreen(navController: NavController) {

    var selectedPeriod by remember { mutableStateOf("Semua") }
    val periods = listOf("Semua", "Jan", "Feb", "Mar", "Apr")

    val totalProfit   = dummyTransaksi.sumOf { it.keuntungan }
    val totalPemilik  = dummyTransaksi.sumOf { it.bagianPemilik }
    val totalPengurus = dummyTransaksi.sumOf { it.bagianPengurus }

    LazyColumn(
        modifier       = Modifier.fillMaxSize().background(BackgroundLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // === RINGKASAN HEADER ===
        item {
            LaporanHeaderCard(totalProfit, totalPemilik, totalPengurus)
        }

        // === FILTER PERIODE ===
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(periods) { p ->
                    FilterChipItem(p, selectedPeriod == p, { selectedPeriod = p })
                }
            }
        }

        // === SECTION TITLE ===
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Riwayat Transaksi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = { /* TODO: Export */ }) {
                    Icon(Icons.Outlined.Download, null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Export", color = GreenPrimary, fontSize = 13.sp)
                }
            }
        }

        // === TRANSAKSI LIST ===
        items(dummyTransaksi) { trx ->
            TransaksiCard(trx = trx)
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun LaporanHeaderCard(totalProfit: Long, totalPemilik: Long, totalPengurus: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GreenPrimaryDark, GreenPrimary)
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Text("Ringkasan Bagi Hasil", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.height(4.dp))
            Text(
                "Rp ${"%,d".format(totalProfit)}",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
            Text("Total Keuntungan dari ${dummyTransaksi.size} transaksi", fontSize = 12.sp, color = Color.White.copy(alpha = 0.65f))

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryBox("💰 Pemilik",  "Rp ${"%.1f".format(totalPemilik / 1_000_000.0)}jt",  modifier = Modifier.weight(1f))
                SummaryBox("🌾 Pengurus", "Rp ${"%.1f".format(totalPengurus / 1_000_000.0)}jt", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun SummaryBox(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(12.dp)
    ) {
        Column {
            Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun TransaksiCard(trx: TransaksiItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick   = { expanded = !expanded },
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (trx.keuntungan > 0) StatusHidupBg else StatusSakitBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (trx.keuntungan > 0) Icons.Filled.TrendingUp else Icons.Filled.TrendingFlat,
                        null,
                        tint     = if (trx.keuntungan > 0) StatusHidup else StatusSakit,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(trx.idDomba, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("${trx.idTrx}  •  ${trx.tanggal}", fontSize = 11.sp, color = TextHint)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Rp ${"%.1f".format(trx.hargaJual / 1_000_000.0)}jt",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary
                    )
                    Text(
                        if (trx.keuntungan > 0) "+Rp ${"%.1f".format(trx.keuntungan / 1_000_000.0)}jt"
                        else "Modal kembali",
                        fontSize = 11.sp,
                        color    = if (trx.keuntungan > 0) StatusHidup else StatusSakit
                    )
                }
            }

            // Expanded detail
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(bottom = 10.dp))
                    Text("Rincian Bagi Hasil", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    BagiHasilRow("Harga Modal",      "Rp ${"%,d".format(trx.hargaModal)}", TextSecondary)
                    BagiHasilRow("Harga Jual",       "Rp ${"%,d".format(trx.hargaJual)}",  TextPrimary)
                    BagiHasilRow("Keuntungan Bersih","Rp ${"%,d".format(trx.keuntungan)}",  StatusHidup)
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 6.dp))
                    BagiHasilRow("💰 Bagian Pemilik  (${trx.pemilik})", "Rp ${"%,d".format(trx.bagianPemilik)}", GreenPrimary, FontWeight.SemiBold)
                    BagiHasilRow("🌾 Bagian Pengurus", "Rp ${"%,d".format(trx.bagianPengurus)}", GreenSecondary, FontWeight.SemiBold)
                }
            }

            // Expand indicator
            Icon(
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint     = TextHint,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp)
                    .size(20.dp)
            )
        }
    }
}

@Composable
fun BagiHasilRow(label: String, value: String, valueColor: Color, fontWeight: FontWeight = FontWeight.Normal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
        Text(value, fontSize = 12.sp, color = valueColor, fontWeight = fontWeight)
    }
}