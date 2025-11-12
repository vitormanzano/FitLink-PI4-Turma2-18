package br.edu.puc.fitlink.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

data class WorkoutItem(val name: String, val series: String)
data class WorkoutGroup(val title: String, val items: List<WorkoutItem>)
data class Trainer(
    val id: String,
    val name: String,
    val specialty: String,
    val avatarUrl: String? = null
)

class AppViewModel : ViewModel() {
    var hasPersonal by mutableStateOf(true)
        private set

    // Dados mockados
    val groupsA = listOf(
        WorkoutItem("Agachamento", "3 séries de 10 repetições"),
        WorkoutItem("Supino", "3 séries de 12 repetições"),
        WorkoutItem("Remada", "3 séries de 15 repetições"),
    )
    val groupsB = listOf(
        WorkoutItem("Leg Press", "3 séries de 10 repetições"),
        WorkoutItem("Desenvolvimento", "3 séries de 12 repetições"),
        WorkoutItem("Puxada Alta", "3 séries de 15 repetições"),
    )
    val workoutGroups = listOf(
        WorkoutGroup("Treino A", groupsA),
        WorkoutGroup("Treino B", groupsB),
    )

    val trainers = listOf(
        Trainer("1", "Carlos Silva", "Hipertrofia"),
        Trainer("2", "Ana Souza", "Funcional", avatarUrl = null),
        Trainer("3", "Lucas Mendes", "Funcional")
    )

    fun connectPersonal() { hasPersonal = true }
    fun disconnectPersonal() { hasPersonal = false }
}