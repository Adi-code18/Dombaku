package com.app.dombaku.ui.theme.screens.pengurus

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.dombaku.ui.theme.*
import androidx.compose.animation.core.tween

import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.dombaku.ui.viewmodel.AuthViewModel
import com.app.dombaku.ui.viewmodel.AuthUiState
import com.app.dombaku.ui.viewmodel.ViewModelFactory
import com.app.dombaku.ui.viewmodel.DashboardViewModel
import com.app.dombaku.ui.viewmodel.DombaListViewModel
import com.app.dombaku.ui.viewmodel.KambingViewModel

data class AdminNavItem(
    val label     : String,
    val route     : String,
    val iconActive: ImageVector,
    val iconIdle  : ImageVector
)

val adminNavItems = listOf(
    AdminNavItem("Home",    "dashboard", Icons.Filled.Home,        Icons.Outlined.Home),
    AdminNavItem("Manage",  "domba",     Icons.Filled.Settings,    Icons.Outlined.Settings),
    AdminNavItem("Account", "account",   Icons.Filled.Person,      Icons.Outlined.Person),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(navController: NavController, factory: ViewModelFactory) {
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val dashViewModel: DashboardViewModel = viewModel(factory = factory)
    val listViewModel: DombaListViewModel = viewModel(factory = factory)
    val kambingViewModel: KambingViewModel = viewModel(factory = factory)

    var selectedTab by remember { mutableStateOf(0) }
    val authState by authViewModel.uiState.collectAsState()
    val namaUser = (authState as? AuthUiState.Success)?.user?.nama ?: "Pengurus"

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = adminNavItems[selectedTab].label,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp,
                            color      = TextOnGreen
                        )
                        Text(
                            text     = "Halo, $namaUser 👋",
                            fontSize = 12.sp,
                            color    = TextOnGreen.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Notifications */ }) {
                        BadgedBox(badge = { Badge { Text("3") } }) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Notifikasi",
                                tint = TextOnGreen
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceLight,
            ) {
                adminNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected  = selectedTab == index,
                        onClick   = { selectedTab = index },
                        icon      = {
                            Icon(
                                if (selectedTab == index) item.iconActive else item.iconIdle,
                                contentDescription = item.label
                            )
                        },
                        label     = {
                            Text(
                                item.label,
                                fontSize   = 11.sp,
                                fontWeight = if (selectedTab == index)
                                    FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors    = NavigationBarItemDefaults.colors(
                            selectedIconColor   = GreenPrimary,
                            selectedTextColor   = GreenPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor      = GreenContainer
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AnimatedContent(
                targetState  = selectedTab,
                transitionSpec = {
                    fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                },
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    0 -> DashboardScreen(navController, dashViewModel)
                    1 -> DombaListScreen(navController, listViewModel, authViewModel, kambingViewModel)
                    2 -> AccountScreen(navController, authViewModel)
                }
            }
        }
    }
}
