package br.edu.puc.fitlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.puc.fitlink.ui.screens.ChooseUseScreen
import br.edu.puc.fitlink.ui.screens.FirstTimeScreen
import br.edu.puc.fitlink.ui.screens.LoginScreen
import br.edu.puc.fitlink.ui.screens.SignUpScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "firstTime"
            ) {
                composable("firstTime") {
                    FirstTimeScreen(navController)
                }
                composable("choose"){
                    ChooseUseScreen(navController)
                }
                composable("login") {
                    LoginScreen(navController)
                }
                composable("signUp"){
                    SignUpScreen(navController)
                }
            }
        }
    }
}
