package br.edu.puc.fitlink.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BottomBar(current: String, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = Color(0xFFFFC107)) {
        NavigationBarItem(
            selected = current == "home",
            onClick = { onNavigate("home") },
            icon = { Icon(Icons.Outlined.FitnessCenter, contentDescription = "Treinos", tint = Color.Black) },
            label = { Text("Treinos", color = Color.Black) }
        )
        NavigationBarItem(
            selected = current == "search",
            onClick = { onNavigate("search") },
            icon = { Icon(Icons.Outlined.Search, contentDescription = "Busca", tint = Color.Black) },
            label = { Text("Busca", color = Color.Black) }
        )
        NavigationBarItem(
            selected = current == "profile",
            onClick = { onNavigate("profile") },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Perfil", tint = Color.Black) },
            label = { Text("Perfil", color = Color.Black) }
        )
    }
}
