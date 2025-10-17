package br.edu.puc.fitlink

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.provider.Settings
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.puc.fitlink.ui.screens.FirstTimeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val PREFS_NAME = "MyPrefsFile"
            val settings = getSharedPreferences(PREFS_NAME, 0)

                val isFirstTime = settings.getBoolean("my_first_time", true)
                if (isFirstTime) {
                    settings.edit().putBoolean("my_first_time", false).apply()
                }

                val startDestination = if (isFirstTime) "firstTime" else "signup"
                val androidId =
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("firstTime") { FirstTimeScreen(navController) }
                    }
                }
            }
        }
