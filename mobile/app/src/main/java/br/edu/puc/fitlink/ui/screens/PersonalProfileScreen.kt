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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.data.model.PersonalResponseDto
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalProfileScreen(
    personalId: String,
    onBack: () -> Unit,
    appViewModel: AppViewModel,                   // <-- agora vem de fora
    vm: PersonalDetailViewModel = viewModel(),
    messageVm: MessageViewModel = viewModel()     // <-- para enviar solicitação
) {
    LaunchedEffect(personalId) {
        vm.loadPersonal(personalId)
    }

    val state = vm.uiState

    // FEEDBACK STATES
    var isSending by remember { mutableStateOf(false) }
    var snackbarMsg by remember { mutableStateOf<String?>(null) }
    var errorDialog by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMsg = null
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                Snackbar(
                    containerColor = FitYellow,
                    contentColor = FitBlack,
                    shape = RoundedCornerShape(12.dp),
                    snackbarData = snackbarData
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.personal?.name ?: "Personal Trainer",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = 70.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FitYellow),
                modifier = Modifier.height(64.dp)
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage != null -> {
                Box(
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.errorMessage ?: "Erro ao carregar", color = Color.Red)
                }
            }

            state.personal != null -> {
                PersonalProfileContent(
                    personal = state.personal,
                    innerPadding = innerPadding,
                    isSending = isSending,
                    onTenhoInteresse = {
                        val clientId = appViewModel.clientId

                        if (clientId.isNullOrBlank()) {
                            errorDialog = "Você precisa estar logado como aluno para enviar uma solicitação."
                            return@PersonalProfileContent
                        }

                        isSending = true

                        messageVm.enviarSolicitacao(
                            clientId = clientId,
                            personalId = personalId
                        ) { ok, msg ->
                            isSending = false
                            if (ok) snackbarMsg = msg
                            else errorDialog = msg
                        }
                    }
                )
            }
        }
    }

    // Dialog de erro
    if (errorDialog != null) {
        AlertDialog(
            onDismissRequest = { errorDialog = null },
            confirmButton = {
                TextButton(onClick = { errorDialog = null }) {
                    Text("OK", color = FitBlack)
                }
            },
            text = { Text(errorDialog!!) }
        )
    }
}

@Composable
fun PersonalProfileContent(
    personal: PersonalResponseDto,
    innerPadding: PaddingValues,
    isSending: Boolean,
    onTenhoInteresse: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.height(16.dp))

        // FOTO + NOME
        Column(
            modifier = Modifier.fillMaxWidth(),
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
                personal.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FitBlack,
                textAlign = TextAlign.Center
            )

            Text(
                personal.specialty ?: "Personal Trainer",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            if (!personal.city.isNullOrBlank()) {
                Text(
                    "Atende em ${personal.city}",
                    color = FitBlack,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(20.dp))

            // BOTÃO -> Tenho interesse
            Button(
                onClick = onTenhoInteresse,
                enabled = !isSending,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSending) Color.LightGray else FitYellow
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = FitBlack,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        "Tenho interesse",
                        color = FitBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }

        // SOBRE
        SectionTitle("Sobre")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = buildString {
                    append("${personal.name} é um personal trainer")
                    if (!personal.specialty.isNullOrBlank()) append(" especializado em ${personal.specialty}")
                    if (!personal.city.isNullOrBlank()) append(", atendendo em ${personal.city}")
                    append(". Trabalha com treinos personalizados focados nos seus objetivos.")
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // PLANOS
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

@Composable
private fun SectionTitle(text: String) {
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
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
