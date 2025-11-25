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
import br.edu.puc.fitlink.ui.screens.StudentRequestScreen
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

                // Rotas base (sem parâmetros) que mostram bottom bar do aluno
                val bottomRoutesAluno = remember { setOf("home", "search", "profile") }

                // Rotas base (sem parâmetros) que mostram bottom bar do personal
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

                // Normaliza rota pra comparar (studentsWorkout/123 -> studentsWorkout)
                val currentBaseRoute = currentRoute?.substringBefore("/")

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    bottomBar = {
                        when {
                            vm.isProfessor && currentBaseRoute in bottomRoutesPersonal -> {
                                BottomBarPersonal(
                                    current = currentBaseRoute ?: "newStudents",
                                    onNavigate = { dest ->
                                        if (dest != currentBaseRoute) {
                                            navController.navigate(dest) {
                                                popUpTo("newStudents") { inclusive = false }
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                )
                            }

                            !vm.isProfessor && currentBaseRoute in bottomRoutesAluno -> {
                                BottomBar(
                                    current = currentBaseRoute ?: "home",
                                    onNavigate = { dest ->
                                        if (dest != currentBaseRoute) {
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

                        // EDITAR PERFIL (ALUNO)
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
                                appViewModel = vm
                            )
                        }

                        // PERSONAL - NOVOS ALUNOS
                        composable("newStudents") {
                            NewStudentsScreen(
                                onAlunoClick = { aluno ->
                                    navController.navigate("studentRequest/${aluno.clientId}/${aluno.messageId}")
                                },
                                appViewModel = vm
                            )
                        }

                        // PERSONAL - MEUS ALUNOS
                        composable("myStudents") {
                            MyStudentsScreen(
                                appViewModel = vm,
                                onAlunoClick = { aluno ->
                                    navController.navigate("studentsDetails/${aluno.id}")
                                }
                            )
                        }

                        composable(
                            "studentRequest/{clientId}/{messageId}",
                            arguments = listOf(
                                navArgument("clientId") { type = NavType.StringType },
                                navArgument("messageId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val clientId = backStackEntry.arguments!!.getString("clientId")!!
                            val messageId = backStackEntry.arguments!!.getString("messageId")!!
                            val personalId = vm.clientId!!   // personal logado

                            StudentRequestScreen(
                                clientId = clientId,
                                messageId = messageId,
                                personalId = personalId,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // PERSONAL - DETALHES DO ALUNO
                        composable(
                            "studentsDetails/{clientId}",
                            arguments = listOf(navArgument("clientId") { type = NavType.StringType })
                        ) { entry ->
                            val clientId = entry.arguments!!.getString("clientId")!!
                            StudentsDetailsScreen(
                                navController = navController,
                                clientId = clientId
                            )
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
                        composable(
                            route = "studentsWorkout/{studentId}",
                            arguments = listOf(
                                navArgument("studentId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val studentId =
                                backStackEntry.arguments?.getString("studentId") ?: ""
                            StudentsWorkoutScreen(
                                navController = navController,
                                studentId = studentId
                            )
                        }

                        // PERSONAL - CRIAR/EDITAR TREINO DO ALUNO
                        composable(
                            route = "editStudentsWorkout/{studentId}",
                            arguments = listOf(
                                navArgument("studentId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
                            EditStudentsWorkoutScreen(
                                navController = navController,
                                studentId = studentId,
                                appViewModel = vm
                            )
                        }
                    }
                }
            }
        }
    }
}
