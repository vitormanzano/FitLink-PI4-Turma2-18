package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.ui.components.TopBar
import br.edu.puc.fitlink.ui.theme.FitBlack

data class AlunoInteressado(
    val nome: String,
    val cidade: String,
    val fotoRes: Int
)

@Composable
fun NewStudentsScreen(
    onAlunoClick: (AlunoInteressado) -> Unit = {}
) {
    val alunos = listOf(
        AlunoInteressado("João Pedro", "Campinas", R.drawable.ic_male),
        AlunoInteressado("Mariana Torres", "Valinhos", R.drawable.ic_female),
        AlunoInteressado("Lucas Almeida", "Sumaré", R.drawable.ic_male),
        AlunoInteressado("Ana Souza", "Campinas", R.drawable.ic_female),
        AlunoInteressado("Rafael Lima", "Paulínia", R.drawable.ic_male)
    )

    Scaffold(
        topBar = { TopBar(title = "Alunos Interessados") },
     ) { innerPadding ->
        if (alunos.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum aluno interessado ainda.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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
