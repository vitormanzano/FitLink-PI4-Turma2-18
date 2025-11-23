package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.Color
import br.edu.puc.fitlink.ui.components.TopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStudentsWorkoutScreen(navController: NavController) {

    var diaSemana by remember { mutableStateOf("Segunda") }

    var exercicio by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var repeticoes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(title = "Editar Exercício", showBack = true, onBack = { navController.popBackStack() })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                // Dropdown de dia da semana
                DayOfWeekDropdown(
                    selectedDay = diaSemana,
                    onDaySelected = { diaSemana = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Exercício
                OutlinedTextField(
                    value = exercicio,
                    onValueChange = { exercicio = it },
                    label = { Text("Exercício") },
                    leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Séries
                OutlinedTextField(
                    value = series,
                    onValueChange = { series = it },
                    label = { Text("Séries") },
                    leadingIcon = { Icon(Icons.Default.AvTimer, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Repetições
                OutlinedTextField(
                    value = repeticoes,
                    onValueChange = { repeticoes = it },
                    label = { Text("Repetições") },
                    leadingIcon = { Icon(Icons.Default.Repeat, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Botão Salvar
                Button(
                    onClick = { /* salvar dados */ },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = FitYellow)
                ) {
                    Text(text = "Salvar", color = FitBlack)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayOfWeekDropdown(
    selectedDay: String,
    onDaySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo")
    var expanded by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val screenWidth = this.maxWidth

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                value = selectedDay,
                onValueChange = { },
                readOnly = true,
                label = { Text("Dia da Semana") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.Black
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(screenWidth) // largura total da tela
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    daysOfWeek.forEach { day ->
                        DropdownMenuItem(
                            text = { Text(day) },
                            onClick = {
                                onDaySelected(day)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
