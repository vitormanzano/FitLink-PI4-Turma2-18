package br.edu.puc.fitlink.ui.screens

import android.util.Log
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import java.io.IOException

// ---------------------------- Personal Detail Screen (arrumada) ----------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDetailScreen(
    personalId: String,
    onBack: () -> Unit,
    appViewModel: AppViewModel,
    vm: PersonalDetailViewModel = viewModel(),
    messageVm: MessageViewModel = viewModel()
) {
    // 1) carrega dados do personal usando o id da rota (fallback)
    LaunchedEffect(personalId) {
        Log.d("PersonalDetailScreen", "Chamando vm.loadPersonal(personalId='$personalId')")
        vm.loadPersonal(personalId)
    }

    val state = vm.uiState

    // Estados de feedback
    var isSending by remember { mutableStateOf(false) }
    var snackbarMsg by remember { mutableStateOf<String?>(null) }
    var errorDialog by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Quando snackbarMsg mudar, mostra snackbar
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMsg = null
        }
    }

    /**
     * Observa mudanças no clientId E no objeto personal carregado.
     * - Se state.personal?.id estiver disponível, usa ele;
     * - Caso contrário, usa o personalId da rota como fallback.
     *
     * Só chama vm.checkIfLinked quando clientId NÃO for nulo/vazio e idToUse estiver disponível.
     */
    LaunchedEffect(Unit) {
        snapshotFlow {
            // pair (clientId, realIdFromStateOrNull)
            val c = appViewModel.clientId
            val real = vm.uiState.personal?.id
            Pair(c, real)
        }
            .distinctUntilChanged()
            .collect { (clientId, realPersonalId) ->
                val idToUse = realPersonalId ?: personalId
                Log.d("PersonalDetailScreen", "Observando mudanças -> clientId='$clientId', realPersonalId='$realPersonalId', fallback='$personalId', using='$idToUse'")

                // chama a verificação apenas se tivermos um id para usar (sempre teremos usando fallback)
                // e se clientId estiver presente (se não estiver, chamamos com null para que o VM trate)
                vm.checkIfLinked(clientId, idToUse)
            }
    }

    // usa o estado exposto pelo ViewModel (null | true | false)
    val isLinked = vm.isLinkedToPersonal

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
                        text = "Personal Trainer",
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
                    Text(
                        state.errorMessage ?: "Erro ao carregar",
                        color = Color.Red
                    )
                }
            }

            state.personal != null -> {
                // log do objeto personal para debugging (pode remover depois)
                LaunchedEffect(state.personal) {
                    Log.d("PersonalDetailScreen", "Objeto personal carregado: ${state.personal}")
                }

                PersonalDetailContent(
                    personal = state.personal,
                    innerPadding = innerPadding,
                    isSending = isSending,
                    isLinked = isLinked, // usa estado do ViewModel
                    onTenhoInteresse = {
                        val clientId = appViewModel.clientId

                        if (clientId.isNullOrBlank()) {
                            errorDialog = "Você precisa estar logado como aluno para enviar uma solicitação."
                            return@PersonalDetailContent
                        }

                        // se já for aluno, não permite enviar solicitação
                        if (isLinked == true) {
                            snackbarMsg = "Você já é aluno desse personal."
                            return@PersonalDetailContent
                        }

                        isSending = true

                        // usa o ID do objeto retornado pelo backend (state.personal.id)
                        val idToSend = state.personal.id
                        Log.d("PersonalDetailScreen", "Enviando solicitação -> clientId='$clientId', personalId='$idToSend'")

                        messageVm.enviarSolicitacao(
                            clientId = clientId,
                            personalId = idToSend
                        ) { ok, msg ->
                            isSending = false
                            if (ok) {
                                snackbarMsg = msg
                            } else {
                                errorDialog = msg
                            }
                        }
                    },
                    onDesfazerVinculo = {
                        val clientId = appViewModel.clientId
                        val personalId = state.personal.id

                        if (clientId.isNullOrBlank()) {
                            errorDialog = "Você precisa estar logado para desfazer o vínculo."
                            return@PersonalDetailContent
                        }

                        isSending = true
                        Log.d("PersonalDetailScreen", "Desfazendo vínculo -> clientId='$clientId', personalId='$personalId'")

                        messageVm.desfazerVinculo(
                            clientId = clientId,
                            personalId = personalId
                        ) { ok, msg ->
                            isSending = false
                            if (ok) {
                                snackbarMsg = "Vínculo desfeito com sucesso!"
                                vm.checkIfLinked(clientId, personalId) // atualiza estado
                            } else {
                                errorDialog = msg
                            }
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


// ---------------------------- Personal Detail Content ----------------------------
@Composable
fun PersonalDetailContent(
    personal: PersonalResponseDto,
    innerPadding: PaddingValues,
    isSending: Boolean,
    isLinked: Boolean?,            // null = verificando, true = já é aluno, false = não é aluno
    onTenhoInteresse: () -> Unit,
    onDesfazerVinculo: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.height(16.dp))

        // ------ FOTO + NOME ------
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

            Spacer(Modifier.height(16.dp))

            // Botão "Tenho interesse" com feedback
            when (isLinked) {
                true -> {
                    Button(
                        onClick = onDesfazerVinculo,
                        enabled = true,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Desfazer Vinculo",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                null -> {
                    // verificando ainda -> mostra loader no lugar do texto (ou desabilitado)
                    Button(
                        onClick = { },
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(48.dp)
                    ) {
                        CircularProgressIndicator(
                            color = FitBlack,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                false -> {
                    // não é aluno -> botão normal
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
                                text = "Tenho interesse",
                                color = FitBlack,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        // ------ SOBRE ------
        SectionTitle("Sobre")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = personal.bio ?: "Sem descrição fornecida pelo personal.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // ------ ESPECIALIZAÇÃO ------
        SectionTitle("Especialização")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = personal.specialty ?: "Não informada",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // ------ EXPERIÊNCIA ------
        SectionTitle("Experiência")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = personal.experience ?: "Não informada",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // ------ PLANOS ------
        SectionTitle("Planos de Treino")

        PlanoCardPersonal(
            titulo = "Plano Basic",
            descricao = "Treino voltado ao ganho de massa muscular com acompanhamento semanal e ajustes personalizados.",
            imagem = R.drawable.img_basico
        )

        Spacer(Modifier.height(12.dp))

        PlanoCardPersonal(
            titulo = "Plano Premium",
            descricao = "Programa completo com foco em hipertrofia, nutrição esportiva e acompanhamento diário via app.",
            imagem = R.drawable.img_premium
        )

        Spacer(Modifier.height(32.dp))
    }
}

// ---------------------------- Section Title ----------------------------
@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp),
        color = FitBlack
    )
}

// ---------------------------- Plano Card ----------------------------
@Composable
fun PlanoCardPersonal(titulo: String, descricao: String, imagem: Int) {
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
