package br.edu.puc.fitlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.puc.fitlink.ui.components.BottomBar
import br.edu.puc.fitlink.ui.components.BottomBarPersonal
import br.edu.puc.fitlink.ui.screens.AppViewModel
import br.edu.puc.fitlink.ui.screens.EditProfileScreen
import br.edu.puc.fitlink.ui.screens.EditStudentsWorkoutScreen
import br.edu.puc.fitlink.ui.screens.FirstTimeScreen
import br.edu.puc.fitlink.ui.screens.LoginScreen
import br.edu.puc.fitlink.ui.screens.MyStudentsScreen
import br.edu.puc.fitlink.ui.screens.MyWorkoutsScreen
import br.edu.puc.fitlink.ui.screens.NewStudentsScreen
import br.edu.puc.fitlink.ui.screens.PersonalDetailScreen
import br.edu.puc.fitlink.ui.screens.SearchAScreen
import br.edu.puc.fitlink.ui.screens.SignUpScreen
import br.edu.puc.fitlink.ui.screens.StudentsDetailsScreen
import br.edu.puc.fitlink.ui.screens.StudentsWorkoutScreen
import br.edu.puc.fitlink.ui.screens.UserProfileScreen
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
                val bottomRoutesPersonal = remember {
                    setOf(
                        "newStudents",
                        "myStudents",
                        "personalProfile",
                        "studentsDetails",
                        "studentsWorkout",
                        "editStudentsWorkout"
                    )
                }

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
                        startDestination = "firstTime",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("firstTime") {
                            FirstTimeScreen(navController)
                        }

                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                onLoginSuccess = { clientId, isProfessor ->
                                    vm.updateClientId(clientId)
                                    vm.setIsProfessor(isProfessor)

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

                        composable("signUp") {
                            SignUpScreen(navController)
                        }

                        // ALUNO - HOME (meus treinos)
                        composable("home") {
                            MyWorkoutsScreen(
                                vm = vm,
                                onFindPersonal = { navController.navigate("search") }
                            )
                        }

                        // ALUNO - BUSCA DE PERSONAL
                        composable("search") {
                            SearchAScreen(
                                onPersonalClick = { personal ->
                                    navController.navigate("personalDetail/${personal.id}")
                                }
                            )
                        }

                        // PERFIL (ALUNO)
                        composable("profile") {
                            UserProfileScreen(
                                navController = navController,
                                appViewModel = vm
                            )
                        }

                        // EDITAR PERFIL (ALUNO) – agora recebendo o AppViewModel correto
                        composable("editProfile") {
                            EditProfileScreen(
                                onBack = { navController.popBackStack() },
                                appViewModel = vm
                            )
                        }

                        // DETALHE DO PERSONAL
                        composable(
                            route = "personalDetail/{personalId}",
                            arguments = listOf(
                                navArgument("personalId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val personalId =
                                backStackEntry.arguments?.getString("personalId") ?: ""

                            PersonalDetailScreen(
                                personalId = personalId,
                                onBack = { navController.popBackStack() },
                                appViewModel = vm   // <-- passa o msm AppViewModel usado no app todo
                            )
                        }

                        // PERSONAL - NOVOS ALUNOS
                        composable("newStudents") {
                            NewStudentsScreen(
                                onAlunoClick = { navController.navigate("studentsDetails") },
                                appViewModel = vm
                            )
                        }
                        // PERSONAL - MEUS ALUNOS
                        composable("myStudents") {
                            MyStudentsScreen(
                                onAlunoClick = { navController.navigate("studentsDetails") }
                            )
                        }

                        // PERSONAL - DETALHES DO ALUNO
                        composable("studentsDetails") {
                            StudentsDetailsScreen(navController)
                        }

                        // PERSONAL - PERFIL
                        composable("personalProfile") {
                            // PersonalProfileContent()
                        }

                        // PERSONAL - EDITAR PERFIL
                        composable("personalEditProfile") {
                            TODO("IMPLEMENTAR TOTALMENTE")
                        }

                        // PERSONAL - VER TREINO DO ALUNO
                        composable("studentsWorkout") {
                            StudentsWorkoutScreen(navController)
                        }

                        // PERSONAL - CRIAR/EDITAR TREINO DO ALUNO
                        composable("editStudentsWorkout") {
                            EditStudentsWorkoutScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
