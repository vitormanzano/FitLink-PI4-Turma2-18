package br.edu.puc.fitlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import br.edu.puc.fitlink.ui.screens.*
import br.edu.puc.fitlink.ui.theme.FitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FitTheme {
                val navController = rememberNavController()
                val vm: AppViewModel = viewModel()

                // ROTAS QUE MOSTRAM A BOTTOM BAR (ALUNO)
                val bottomRoutesAluno = remember {
                    setOf("home", "search", "profile")
                }

                // ROTAS QUE MOSTRAM A BOTTOM BAR (PERSONAL)
                val bottomRoutesPersonal = remember {
                    setOf("newStudents", "myStudents", "personalProfile")
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val currentBaseRoute = currentRoute?.substringBefore("/")

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    bottomBar = {

                        // ------ PERSONAL ------
                        if (vm.isProfessor && currentBaseRoute in bottomRoutesPersonal) {
                            BottomBarPersonal(
                                current = currentBaseRoute ?: "newStudents",
                                onNavigate = { dest ->

                                    val personalId = vm.clientId

                                    if (dest == "personalProfile") {
                                        if (personalId != null) {
                                            navController.navigate("personalProfile/$personalId") {
                                                popUpTo("newStudents") { inclusive = false }
                                                launchSingleTop = true
                                            }
                                        }
                                    } else {
                                        navController.navigate(dest) {
                                            popUpTo("newStudents") { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            )
                        }

                        // ------ ALUNO ------
                        else if (!vm.isProfessor && currentBaseRoute in bottomRoutesAluno) {
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

                        // ----------------------- ALUNO ----------------------

                        composable("home") {
                            MyWorkoutsScreen(
                                vm = vm,
                                onFindPersonal = { navController.navigate("search") }
                            )
                        }

                        composable("search") {
                            SearchAScreen(
                                onPersonalClick = { personal ->
                                    navController.navigate("personalDetail/${personal.id}")
                                }
                            )
                        }

                        composable("profile") {
                            UserProfileScreen(navController, vm)
                        }

                        composable("editProfile") {
                            EditProfileScreen(
                                onBack = { navController.popBackStack() },
                                appViewModel = vm
                            )
                        }

                        composable(
                            "personalDetail/{personalId}",
                            arguments = listOf(navArgument("personalId") { type = NavType.StringType })
                        ) {
                            val personalId = it.arguments!!.getString("personalId")!!
                            PersonalDetailScreen(
                                personalId = personalId,
                                onBack = { navController.popBackStack() },
                                appViewModel = vm
                            )
                        }

                        // ----------------------- PERSONAL ----------------------

                        composable("newStudents") {
                            NewStudentsScreen(
                                onAlunoClick = { aluno ->
                                    navController.navigate("studentRequest/${aluno.clientId}/${aluno.messageId}")
                                },
                                appViewModel = vm
                            )
                        }

                        composable("myStudents") {
                            MyStudentsScreen(
                                appViewModel = vm,
                                onAlunoClick = { aluno ->
                                    navController.navigate("studentsDetails/${aluno.id}")
                                }
                            )
                        }

                        composable(
                            "personalProfile/{personalId}",
                            arguments = listOf(navArgument("personalId") { type = NavType.StringType })
                        ) {
                            val personalId = it.arguments!!.getString("personalId")!!
                            PersonalUserProfileScreen(
                                navController = navController,
                                personalId = personalId
                            )
                        }

                        composable(
                            "editPersonal/{personalId}",
                            arguments = listOf(navArgument("personalId") { type = NavType.StringType })
                        ) {
                            val personalId = it.arguments!!.getString("personalId")!!

                            PersonalProfileEditScreen(
                                onBack = { navController.popBackStack() },
                                personalId = personalId
                            )
                        }


                        composable(
                            "studentRequest/{clientId}/{messageId}",
                            arguments = listOf(
                                navArgument("clientId") { type = NavType.StringType },
                                navArgument("messageId") { type = NavType.StringType }
                            )
                        ) {
                            val clientId = it.arguments!!.getString("clientId")!!
                            val messageId = it.arguments!!.getString("messageId")!!
                            val personalId = vm.clientId!!

                            StudentRequestScreen(
                                clientId = clientId,
                                messageId = messageId,
                                personalId = personalId,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            "studentsDetails/{clientId}",
                            arguments = listOf(navArgument("clientId") { type = NavType.StringType })
                        ) {
                            val clientId = it.arguments!!.getString("clientId")!!
                            StudentsDetailsScreen(navController, clientId)
                        }

                        composable(
                            "studentsWorkout/{studentId}",
                            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
                        ) {
                            val studentId = it.arguments!!.getString("studentId")!!
                            StudentsWorkoutScreen(navController, studentId)
                        }

                        composable(
                            "editStudentsWorkout/{studentId}",
                            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
                        ) {
                            val studentId = it.arguments!!.getString("studentId")!!
                            EditStudentsWorkoutScreen(navController, studentId, vm)
                        }
                    }
                }
            }
        }
    }
}
