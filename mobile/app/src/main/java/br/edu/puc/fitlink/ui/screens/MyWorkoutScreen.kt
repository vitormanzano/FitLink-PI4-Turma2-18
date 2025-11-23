package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.edu.puc.fitlink.ui.components.IconCard
import br.edu.puc.fitlink.ui.components.PrimaryPillButton
import br.edu.puc.fitlink.ui.components.TopBar
import br.edu.puc.fitlink.ui.theme.FitBlack

@Composable
fun MyWorkoutsScreen(
    vm: AppViewModel,
    onFindPersonal: () -> Unit
) {
    val clientId = vm.clientId

    LaunchedEffect(clientId, vm.hasPersonal) {
        if (vm.hasPersonal && clientId != null) {
            vm.loadWorkouts()
        }
    }

    Column(Modifier.fillMaxSize()) {
        TopBar(title = "Meus Treinos")

        when {
            !vm.hasPersonal -> EmptyState(onFindPersonal)

            vm.isLoading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            vm.errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(vm.errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    PrimaryPillButton(
                        text = "Tentar novamente",
                        onClick = { vm.loadWorkouts() }
                    )
                }
            }

            vm.workoutGroups.isEmpty() -> {
                NoWorkoutState(onFindPersonal)
            }

            else -> WorkoutList(vm)
        }
    }
}

@Composable
private fun EmptyState(onFindPersonal: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Conecte-se com um personal para acessar seus treinos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        PrimaryPillButton(
            text = "Procurar um Personal",
            onClick = onFindPersonal
        )
    }
}

@Composable
private fun WorkoutList(vm: AppViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        vm.workoutGroups.forEach { group ->
            item {
                Spacer(Modifier.height(12.dp))
                Text(
                    group.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(8.dp))
            }
            items(group.items) { it ->
                IconCard {
                    Icon(Icons.Outlined.FitnessCenter, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(it.name, fontWeight = FontWeight.SemiBold, color = FitBlack)
                        Text(it.series, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun NoWorkoutState(onFindPersonal: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Você ainda não possui treinos cadastrados.",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Conecte-se com um personal para receber seu primeiro treino!",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        PrimaryPillButton(
            text = "Encontrar Personal",
            onClick = onFindPersonal
        )
    }
}
