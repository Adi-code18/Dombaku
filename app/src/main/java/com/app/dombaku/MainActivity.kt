package com.app.dombaku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.dombaku.ui.theme.DombaKuTheme
import com.app.dombaku.ui.viewmodel.ViewModelFactory
import com.app.dombaku.ui.theme.screens.auth.SplashScreen
import com.app.dombaku.ui.theme.screens.auth.LoginScreen
import com.app.dombaku.ui.theme.screens.auth.RegisterScreen
import com.app.dombaku.ui.theme.screens.pengurus.AdminMainScreen
import com.app.dombaku.ui.theme.screens.pemilik.InvestorMainScreen
import com.app.dombaku.ui.theme.screens.pengurus.TambahDombaScreen
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as DombakuApp
        val factory = ViewModelFactory(
            app.authRepository,
            app.kambingRepository,
            app.transaksiKambingRepository,
            app.kelahiranRepository,
            app.apiService
        )

        setContent {
            DombaKuTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(navController)
                    }
                    composable("login") {
                        LoginScreen(navController, viewModel(factory = factory))
                    }
                    composable("register") {
                        RegisterScreen(navController, viewModel(factory = factory))
                    }
                    composable("admin") {
                        AdminMainScreen(navController, factory)
                    }
                    composable("investor") {
                        InvestorMainScreen(navController, factory)
                    }
                    composable("tambah_domba") {
                        val kambingViewModel: com.app.dombaku.ui.viewmodel.KambingViewModel = viewModel(factory = factory)
                        val authViewModel: com.app.dombaku.ui.viewmodel.AuthViewModel = viewModel(factory = factory)
                        TambahDombaScreen(navController, kambingViewModel, authViewModel)
                    }
                }
            }
        }
    }
}
