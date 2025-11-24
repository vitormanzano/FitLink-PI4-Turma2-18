package br.edu.puc.fitlink.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Whatsapp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.data.model.ClientResponseDto
import br.edu.puc.fitlink.data.remote.RetrofitInstance
import br.edu.puc.fitlink.ui.components.TopBar
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow

@Composable
fun StudentsDetailsScreen(
    navController: NavHostController,
    clientId: String
) {
    val context = LocalContext.current

    var aluno by remember { mutableStateOf<ClientResponseDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // BUSCA DADOS REAIS DO ALUNO
    LaunchedEffect(clientId) {
        try {
            val resp = RetrofitInstance.clientApi.getById(clientId)
            if (resp.isSuccessful) {
                aluno = resp.body()
            } else {
                error = "Erro ao carregar aluno."
            }
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Perfil do Aluno",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        }
    ) { inner ->

        if (isLoading) {
            Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        if (error != null || aluno == null) {
            Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text(error ?: "Erro", color = Color.Red) }
            return@Scaffold
        }

        val a = aluno!!

        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            Image(
                painter = painterResource(R.drawable.ic_male),
                contentDescription = "Foto do aluno",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = a.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(16.dp))

            // ==================== AÇÕES ========================
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Entrar em contato
                Button(
                    onClick = {
                        val url = "https://wa.me/${a.phone}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FitYellow),
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Whatsapp, contentDescription = null, tint = FitBlack)
                        Spacer(Modifier.width(8.dp))
                        Text("Entrar em Contato", color = FitBlack, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Editar treino
                OutlinedButton(
                    onClick = { navController.navigate("studentsWorkout") },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FitBlack),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Text("Editar Treino", color = FitBlack, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(28.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {

                SectionTitle("Sobre")
                Text(a.aboutMe ?: "Sem descrição", style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(24.dp))

                SectionTitle("Objetivos")
                Text(a.goals ?: "Não informado", fontWeight = FontWeight.ExtraBold)

                Spacer(Modifier.height(24.dp))

                SectionTitle("Medidas")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column {
                        Text("Altura", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("${a.metrics?.height ?: "-"} m")
                    }

                    Column {
                        Text("Peso", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("${a.metrics?.weight ?: "-"} kg")
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold
    )
}
