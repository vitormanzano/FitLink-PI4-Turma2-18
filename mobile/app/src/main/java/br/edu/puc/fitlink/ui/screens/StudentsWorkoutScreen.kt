package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import br.edu.puc.fitlink.ui.components.TopBar
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsWorkoutScreen(
    navController: NavHostController,
    studentId: String,
    vm: StudentsWorkoutViewModel = viewModel()
) {

    LaunchedEffect(studentId) {
        println("DEBUG_STUDENT_SCREEN → carregando treinos do aluno: $studentId")
        vm.loadWorkoutsForStudent(studentId)
    }

    Scaffold(
        topBar = {
            TopBar(title = "Treinos do Aluno")
        },
        bottomBar = {
            BottomNavigationBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    println("DEBUG_STUDENT_SCREEN → Navegando para editStudentsWorkout/$studentId")
                    navController.navigate("editStudentsWorkout/$studentId")
                },
                containerColor = FitYellow
            ) {
                Text("+", color = FitBlack, fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            when {
                vm.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                vm.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "ERRO: ${vm.errorMessage}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                vm.workoutGroups.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sem treinos cadastrados para esse aluno.")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxSize()
                    ) {
                        items(vm.workoutGroups) { group ->

                            println("DEBUG_STUDENT_SCREEN → exibindo treino: ${group.title}")

                            WorkoutGroupSection(
                                group = group,
                                onDelete = { item ->
                                    println("DEBUG_STUDENT_SCREEN → remover exercício: ${item.name}")
                                    vm.removeExercise(group, item)
                                }
                            )

                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutGroupSection(
    group: WorkoutGroup,
    onDelete: (WorkoutItem) -> Unit
) {
    Text(
        group.title,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        group.items.forEach { item ->
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
                        Text(item.name, fontWeight = FontWeight.Bold, color = FitBlack)
                        Text(item.series) // ex: "3 séries de 10 repetições"
                    }

                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Remover",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDelete(item) }
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
