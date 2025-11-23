package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDetailScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Trainer", fontWeight = FontWeight.Bold, modifier = Modifier.offset(x = (70).dp)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FitYellow),
                modifier = Modifier.height(64.dp),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // --- Cabeçalho com imagem e nome ---
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_male),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Carlos Silva",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FitBlack,
                    textAlign = TextAlign.Center
                )

                Text(
                    "Personal Trainer",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Text(
                    "Especialista em Treinamento para Hipertrofia",
                    color = FitBlack,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { /* ação de interesse */ },
                    colors = ButtonDefaults.buttonColors(containerColor = FitYellow),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .align(Alignment.CenterHorizontally) // garante centralização mesmo dentro de outro layout
                ) {
                    Text("Tenho interesse", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(20.dp))
            }


            // --- Seção Sobre ---
            SectionTitle("Sobre")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Carlos Silva é um personal trainer certificado, com mais de 7 anos de experiência em treinamento de força e hipertrofia muscular. Especialista em métodos avançados de musculação, Carlos ajuda seus alunos a alcançar seus objetivos de ganho de massa magra de forma segura e eficiente, com planos personalizados e acompanhamento técnico completo.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            SectionTitle("Planos de Treino")
            PlanoCard(
                titulo = "Plano Basic",
                descricao = "Treino voltado ao ganho de massa muscular com acompanhamento semanal e ajustes personalizados.",
                imagem = R.drawable.img_basico
            )
            Spacer(Modifier.height(12.dp))
            PlanoCard(
                titulo = "Plano Premium",
                descricao = "Programa completo com foco em hipertrofia, nutrição esportiva e acompanhamento diário via app.",
                imagem = R.drawable.img_premium
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp),
        color = FitBlack
    )
}

@Composable
fun PlanoCard(titulo: String, descricao: String, imagem: Int) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(12.dp)) {
                Text(titulo, fontWeight = FontWeight.Bold, color = FitBlack)
                Text(descricao, style = MaterialTheme.typography.bodySmall)
            }
            Image(
                painter = painterResource(imagem),
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
            )
        }
    }
}
