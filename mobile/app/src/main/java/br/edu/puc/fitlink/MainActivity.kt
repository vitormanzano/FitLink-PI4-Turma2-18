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
import br.edu.puc.fitlink.ui.components.BottomBarPersonal
import br.edu.puc.fitlink.ui.screens.*
import br.edu.puc.fitlink.ui.theme.FitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitTheme {
                val navController = rememberNavController()
                val vm: AppViewModel = viewModel()


                // Rotas do aluno
                val bottomRoutesAluno = remember { setOf("home", "search", "profile") }
                // Rotas do personal
                val bottomRoutesPersonal = remember { setOf("newStudents", "myStudents", "profile") }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    bottomBar = {
                        when {
                            vm.isProfessor && currentRoute in bottomRoutesPersonal -> {
                                BottomBarPersonal(
                                    current = currentRoute ?: "newStudents",
                                    onNavigate = { dest ->
                                        if (dest != currentRoute) {
                                            navController.navigate(dest) {
                                                popUpTo("newStudents") { inclusive = false }
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                )
                            }

                            !vm.isProfessor && currentRoute in bottomRoutesAluno -> {
                                BottomBar(
                                    current = currentRoute ?: "home",
                                    onNavigate = { dest ->
                                        if (dest != currentRoute) {
                                            navController.navigate(dest) {
                                                popUpTo("home") { inclusive = false }
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "newStudents",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("firstTime") { FirstTimeScreen(navController) }
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                onLoginSuccess = { clientId, isProfessor ->
                                    vm.updateClientId(clientId)
                                    vm.setIsProfessor(isProfessor) // 👈 salva no ViewModel

                                    if (isProfessor) {
                                        navController.navigate("newStudents") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
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
                        composable("newStudents") { NewStudentsScreen() }
                        composable("myStudents") { }


                    }
                }
            }
        }
    }
}
