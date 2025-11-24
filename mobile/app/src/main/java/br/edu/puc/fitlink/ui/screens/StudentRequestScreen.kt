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
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.data.model.ClientResponseDto
import br.edu.puc.fitlink.ui.components.TopBar
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow
import br.edu.puc.fitlink.data.remote.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun StudentRequestScreen(
    clientId: String,
    messageId: String,
    personalId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var aluno by remember { mutableStateOf<ClientResponseDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Carrega informações reais do aluno
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
            TopBar(title = "Solicitação do Aluno", showBack = true, onBack = onBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        if (error != null) {
            Box(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(error ?: "Erro", color = Color.Red)
            }
            return@Scaffold
        }

        val alunoData = aluno!!

        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            // Foto
            Image(
                painter = painterResource(R.drawable.ic_male),
                contentDescription = "Foto do aluno",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(12.dp))

            // Nome real do aluno
            Text(
                text = alunoData.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(16.dp))

            // Botões principais
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Entrar em contato
                Button(
                    onClick = {
                        val whatsappUrl = "https://wa.me/${alunoData.phone}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                        context.startActivity(intent)
                    },
                    enabled = !isProcessing,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FitYellow),
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Whatsapp, contentDescription = null, tint = FitBlack)
                    Spacer(Modifier.width(8.dp))
                    Text("Entrar em Contato", color = FitBlack, fontWeight = FontWeight.SemiBold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ACEITAR
                    Button(
                        onClick = {
                            scope.launch {
                                isProcessing = true
                                try {
                                    val resp = RetrofitInstance.clientApi.linkPersonal(
                                        clientId, personalId
                                    )
                                    if (resp.isSuccessful) {
                                        // remove message
                                        RetrofitInstance.messageApi.deleteMessage(messageId)
                                        snackbarHostState.showSnackbar("Aluno aceito com sucesso!")
                                        onBack()
                                    } else {
                                        snackbarHostState.showSnackbar("Erro ao aceitar aluno.")
                                    }
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        enabled = !isProcessing,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier
                            .height(40.dp)
                            .weight(1f)
                    ) { Text("Aceitar", color = Color.White, fontWeight = FontWeight.Bold) }

                    // RECUSAR
                    Button(
                        onClick = {
                            scope.launch {
                                isProcessing = true
                                try {
                                    RetrofitInstance.messageApi.deleteMessage(messageId)
                                    snackbarHostState.showSnackbar("Solicitação recusada.")
                                    onBack()
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        enabled = !isProcessing,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                        modifier = Modifier
                            .height(40.dp)
                            .weight(1f)
                    ) { Text("Recusar", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }

            Spacer(Modifier.height(28.dp))

            // SESSÕES DE PERFIL
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                SectionTitle("Sobre")
                Text(alunoData.aboutMe ?: "Sem descrição", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(24.dp))

                SectionTitle("Objetivos")
                Text(alunoData.goals ?: "Não informado", fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(6.dp))

                Spacer(Modifier.height(24.dp))

                SectionTitle("Medidas")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column {
                        Text("Altura", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("${alunoData.metrics?.height ?: "-"} m")
                    }

                    Column {
                        Text("Peso", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("${alunoData.metrics?.weight ?: "-"} kg")
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
