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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.dombaku.ui.theme.*
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.automirrored.outlined.Logout
import com.app.dombaku.ui.theme.screens.pengurus.DombaListScreen
import com.app.dombaku.ui.theme.screens.pengurus.LaporanScreen
import com.app.dombaku.ui.theme.screens.pemilik.SilsilahScreen

import com.app.dombaku.data.model.Kambing
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.dombaku.ui.viewmodel.DashboardUiState
import com.app.dombaku.ui.viewmodel.DashboardViewModel
import com.app.dombaku.ui.viewmodel.AuthViewModel
import com.app.dombaku.ui.viewmodel.ViewModelFactory
import com.app.dombaku.ui.viewmodel.SilsilahViewModel
import com.app.dombaku.ui.viewmodel.DombaListViewModel
import com.app.dombaku.ui.viewmodel.KambingViewModel
import com.app.dombaku.ui.theme.screens.pengurus.AccountScreen
import com.app.dombaku.ui.theme.screens.pengurus.DombaCard
import com.app.dombaku.ui.theme.screens.pengurus.DombaDetailScreen
import com.app.dombaku.ui.viewmodel.AuthUiState
import com.app.dombaku.utils.Kalkulator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestorMainScreen(
    navController: NavController,
    factory: ViewModelFactory
) {
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val dashViewModel: DashboardViewModel = viewModel(factory = factory)
    val listViewModel: DombaListViewModel = viewModel(factory = factory)
    val silsilahViewModel: SilsilahViewModel = viewModel(factory = factory)
    val kambingViewModel: KambingViewModel = viewModel(factory = factory)

    var selectedTab by remember { mutableStateOf(0) }
    val authState by authViewModel.uiState.collectAsState()
    val user = (authState as? AuthUiState.Success)?.user
    val namaPemilik = user?.nama ?: "Investor"
    val rawRole = user?.role ?: "pemilik"
    val roleDisplay = when(rawRole.lowercase()) {
        "pengurus" -> "Pengurus"
        "pemilik" -> "Pemilik"
        else -> rawRole.replaceFirstChar { it.uppercase() }
    }

    val navItems = listOf(
        Triple("Dashboard", Icons.Filled.Dashboard,  Icons.Outlined.Dashboard),
        Triple("Domba Saya",Icons.Filled.Pets,       Icons.Outlined.Pets),
        Triple("Silsilah",  Icons.Filled.AccountTree,Icons.Outlined.AccountTree),
        Triple("Laporan",   Icons.Filled.BarChart,   Icons.Outlined.BarChart),
        Triple("Akun",      Icons.Filled.Person,     Icons.Outlined.Person),
    )

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Halo, $namaPemilik", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            Text("$roleDisplay  •  Aktif", fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f))
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        authViewModel.resetState()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Outlined.Logout, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = SurfaceLight, tonalElevation = 8.dp) {
                navItems.forEachIndexed { index, (label, iconActive, iconIdle) ->
                    NavigationBarItem(
                        selected  = selectedTab == index,
                        onClick   = { selectedTab = index },
                        icon      = { Icon(if (selectedTab == index) iconActive else iconIdle, label) },
                        label     = { Text(label, fontSize = 10.sp) },
                        colors    = NavigationBarItemDefaults.colors(
                            selectedIconColor   = Color(0xFF2E7D32),
                            selectedTextColor   = Color(0xFF2E7D32),
                            unselectedIconColor = TextSecondary,
                            indicatorColor      = Color(0xFFC8E6C9)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AnimatedContent(
                targetState  = selectedTab,
                transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(180)) },
                label = "investorTab"
            ) { tab ->
                when (tab) {
                    0 -> InvestorDashboardScreen(navController, dashViewModel, authViewModel, kambingViewModel)
                    1 -> InvestorDombaScreen(navController, listViewModel, authViewModel, kambingViewModel)
                    2 -> SilsilahScreen(navController, silsilahViewModel)
                    3 -> LaporanScreen(navController)
                    4 -> AccountScreen(navController, authViewModel)
                }
            }
        }
    }
}

@Composable
fun InvestorDashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel,
    authViewModel: AuthViewModel,
    kambingViewModel: KambingViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedKambing by remember { mutableStateOf<Kambing?>(null) }

    when (uiState) {
        is DashboardUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        }
        is DashboardUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text((uiState as DashboardUiState.Error).message, color = Color.Red)
            }
        }
        is DashboardUiState.Success -> {
            val state = uiState as DashboardUiState.Success
            val authState by authViewModel.uiState.collectAsState()
            val userId = (authState as? AuthUiState.Success)?.user?.uid ?: ""
            val myKambing = state.list.filter { it.pemilikId == userId }

            LazyColumn(
                modifier       = Modifier.fillMaxSize().background(BackgroundLight),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { InvestorPortfolioCard(myKambing) }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InvestorStatMini("🐏 Jantan", "${myKambing.count { it.jenisKelamin.lowercase() == "jantan" && it.statusKondisi.lowercase() != "mati" }} ekor", JantanColor, JantanBg, Modifier.weight(1f))
                        InvestorStatMini("🐑 Betina", "${myKambing.count { it.jenisKelamin.lowercase() == "betina" && it.statusKondisi.lowercase() != "mati" }} ekor", BetinaColor, BetinaBg, Modifier.weight(1f))
                        InvestorStatMini("   Anakan", "${myKambing.count { (it.idIndukJantan != null || it.idIndukBetina != null) && it.statusKondisi.lowercase() != "mati" }} ekor",  GreenPrimary, GreenContainer, Modifier.weight(1f))
                    }
                }

                item { EstimasiBagiHasilCard(myKambing) }

                item {
                    Text("Domba Aktif Milik Saya", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                }
                items(myKambing.filter { it.statusKondisi.lowercase() != "mati" && it.statusKondisi.lowercase() != "terjual" }.take(5)) { kambing ->
                    DombaCard(kambing = kambing, onClick = { selectedKambing = kambing })
                }
                item {
                    TextButton(onClick = { /* Navigate to tab 1 */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Lihat semua domba →", color = GreenPrimary, fontWeight = FontWeight.Medium)
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
fun InvestorPortfolioCard(myKambing: List<Kambing>) {
    val totalAktif = myKambing.count { it.statusKondisi.lowercase() != "mati" && it.statusKondisi.lowercase() != "terjual" }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
                )
            )
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )
        Column {
            Text("Total Domba Aktif Saya", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.height(6.dp))
            Text("$totalAktif Ekor", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("${myKambing.count { it.statusKondisi.lowercase() == "terjual" }} ekor telah terjual", fontSize = 12.sp, color = Color.White.copy(alpha = 0.65f))
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PortfolioChip("💸 Terjual: ${myKambing.count { it.statusKondisi.lowercase() == "terjual" }}")
                PortfolioChip("📈 Sehat: ${myKambing.count { it.statusKondisi.lowercase() == "sehat" || it.statusKondisi.lowercase() == "hidup" }}")
            }
        }
    }
}

@Composable
fun PortfolioChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
fun InvestorStatMini(label: String, value: String, color: Color, bgColor: Color, modifier: Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(label.take(2), fontSize = 16.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(label.drop(2).trim(), fontSize = 10.sp, color = TextSecondary)
        }
    }
}

@Composable
fun EstimasiBagiHasilCard(myKambing: List<Kambing>) {
    val jantanSiapJual = myKambing.count { (it.statusKondisi.lowercase() == "sehat" || it.statusKondisi.lowercase() == "hidup") && it.jenisKelamin.lowercase() == "jantan" }
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = GreenContainer),
        border    = BorderStroke(1.dp, GreenAccent)
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.AccountBalance, null, tint = GreenPrimaryDark, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Estimasi Bagi Hasil", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = GreenPrimaryDark)
                Text("$jantanSiapJual ekor jantan potensial", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun InvestorDombaScreen(
    navController: NavController,
    viewModel: DombaListViewModel,
    authViewModel: AuthViewModel,
    kambingViewModel: KambingViewModel
) {
    val authState by authViewModel.uiState.collectAsState()
    val userId = (authState as? AuthUiState.Success)?.user?.uid ?: ""

    DombaListScreen(
        navController = navController,
        viewModel = viewModel,
        authViewModel = authViewModel,
        kambingViewModel = kambingViewModel,
        ownerId = userId
    )
}
