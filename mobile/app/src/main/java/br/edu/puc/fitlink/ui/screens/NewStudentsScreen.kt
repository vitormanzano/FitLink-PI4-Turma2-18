package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.ui.components.TopBar
import br.edu.puc.fitlink.ui.theme.FitBlack

@Composable
fun NewStudentsScreen(
    onAlunoClick: (AlunoInteressado) -> Unit = {},
    appViewModel: AppViewModel,
    vm: NewStudentsViewModel = viewModel()
) {
    val alunos = vm.alunos
    val isLoading = vm.isLoading
    val error = vm.errorMessage

    val personalId = appViewModel.clientId

    LaunchedEffect(personalId) {
        if (!personalId.isNullOrBlank()) {
            vm.loadInteressados(personalId)
        }
    }

    Scaffold(
        topBar = { TopBar(title = "Alunos Interessados") },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(error, color = Color.Red)
                    }
                }

                alunos.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhum aluno interessado ainda.", color = Color.Gray)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(alunos) { aluno ->
                            AlunoItem(aluno) { onAlunoClick(aluno) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlunoItem(
    aluno: AlunoInteressado,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Image(
                painter = painterResource(aluno.fotoRes),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    aluno.nome,
                    fontWeight = FontWeight.Bold,
                    color = FitBlack
                )
                Text(
                    aluno.cidade,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}
