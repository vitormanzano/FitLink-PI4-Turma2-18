package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.puc.fitlink.ui.components.TopBar
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow

data class Exercise(val name: String, val series: Int, val repetitions: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsWorkoutScreen(navController: NavHostController) {
    var mondayExercises by remember { mutableStateOf(
        mutableListOf(
            Exercise("Agachamento", 3, 10),
            Exercise("Supino", 3, 12),
            Exercise("Remada", 3, 15)
        )
    ) }

    var tuesdayExercises by remember { mutableStateOf(
        mutableListOf(
            Exercise("Leg Press", 3, 10),
            Exercise("Desenvolvimento", 3, 12)
        )
    ) }

    Scaffold(
        topBar = {
            TopBar(title = "Treinos do Aluno")
        },
        bottomBar = {
            BottomNavigationBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("editStudentsWorkout") },
                containerColor = FitYellow
            ) {
                Text("+", color = FitBlack, fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                DaySection(day = "Segunda-feira", exercises = mondayExercises) {
                    mondayExercises.remove(it)
                }
            }

            item {
                DaySection(day = "Terça-feira", exercises = tuesdayExercises) {
                    tuesdayExercises.remove(it)
                }
            }

            item {
                Spacer(Modifier.height(80.dp)) // espaço para o FAB
            }
        }
    }
}

@Composable
fun DaySection(day: String, exercises: List<Exercise>, onDelete: (Exercise) -> Unit) {
    Text(day, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        exercises.forEach { exercise ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        Icons.Outlined.FitnessCenter,
                        contentDescription = null,
                        tint = FitBlack,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(exercise.name, fontWeight = FontWeight.Bold, color = FitBlack)
                        Text("${exercise.series} séries de ${exercise.repetitions} repetições")
                    }

                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Remover",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDelete(exercise) }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(containerColor = FitYellow) {
        NavigationBarItem(
            icon = { /* ícone busca */ },
            label = { Text("Busca") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { /* ícone meus alunos */ },
            label = { Text("Meus Alunos") },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = { /* ícone perfil */ },
            label = { Text("Perfil") },
            selected = false,
            onClick = { }
        )
    }
}
