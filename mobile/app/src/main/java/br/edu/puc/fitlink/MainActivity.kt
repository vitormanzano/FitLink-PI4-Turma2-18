package br.edu.puc.fitlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import br.edu.puc.fitlink.ui.components.BottomBar
import br.edu.puc.fitlink.ui.screens.*
import br.edu.puc.fitlink.ui.theme.FitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitTheme {
                val navController = rememberNavController()
                val vm: AppViewModel = viewModel()

                // Rotas que exibem BottomBar
                val bottomRoutes = remember { setOf("home", "search", "progress", "profile") }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    bottomBar = {
                        if (currentRoute in bottomRoutes) {
                            BottomBar(current = currentRoute ?: "home") { dest ->
                                if (dest != currentRoute) {
                                    navController.navigate(dest) {
                                        popUpTo("home") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "firstTime",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("firstTime") { FirstTimeScreen(navController) }
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                onLoginSuccess = { clientId ->
                                    vm.updateClientId(clientId)      // salva o ID no AppViewModel
                                    vm.connectPersonal()          // opcional: já marca que tem personal

                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("signUp") { SignUpScreen(navController) }

                        composable("home") {
                            MyWorkoutsScreen(
                                vm = vm,
                                onFindPersonal = { navController.navigate("search") }
                            )
                        }

                        composable("search") { SearchAScreen() }
                        composable("profile") { }

                        composable("personalDetail") {
                            PersonalDetailScreen(onBack = { navController.popBackStack() })
                        }


                    }
                }
            }
        }
    }
}
