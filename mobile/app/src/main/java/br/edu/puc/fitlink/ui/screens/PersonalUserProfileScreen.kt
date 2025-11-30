package br.edu.puc.fitlink.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.data.model.PersonalResponseDto
import br.edu.puc.fitlink.ui.components.BottomBar
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow
import kotlinx.coroutines.launch

@Composable
fun PersonalUserProfileScreen(
    navController: NavHostController,
    personalId: String,
    appViewModel: AppViewModel = viewModel(),
    personalViewModel: PersonalViewModel = viewModel()
) {
    val state by personalViewModel.uiState.collectAsState()

    LaunchedEffect(personalId) {
        personalViewModel.loadPersonal(personalId)
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                current = "profile",
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(FitYellow)
            ) {
                Text(
                    "Perfil do Personal",
                    style = MaterialTheme.typography.titleLarge,
                    color = FitBlack,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { inner ->

        val personalLocal = state.personal

        when {
            state.isLoading -> {
                Box(
                    Modifier
                        .padding(inner)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.errorMessage != null -> {
                Box(
                    Modifier
                        .padding(inner)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.errorMessage ?: "Erro ao carregar", color = MaterialTheme.colorScheme.error)
                }
            }
            personalLocal != null -> {
                PersonalProfileView(
                    personal = personalLocal,
                    innerPadding = inner,
                    onEdit = { navController.navigate("editPersonal/$personalId") }
                )
            }
            else -> {
                Box(
                    Modifier
                        .padding(inner)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Personal não encontrado")
                }
            }
        }
    }
}

@Composable
private fun PersonalProfileView(personal: PersonalResponseDto, innerPadding: PaddingValues, onEdit: () -> Unit) {
    Column(
        Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))

        Image(
            painter = painterResource(R.drawable.ic_male),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.height(12.dp))

        Text(personal.name ?: "", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(personal.specialty ?: "Personal Trainer")
        if (!personal.city.isNullOrBlank()) Text("Atende em ${personal.city}")

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onEdit,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FitYellow)
        ) {
            Text("Editar Perfil", color = FitBlack)
        }

        Spacer(Modifier.height(20.dp))

        SectionTitle("Sobre")
        Text(personal.bio ?: "Sem descrição", modifier = Modifier.padding(top = 8.dp))

        Spacer(Modifier.height(12.dp))
        SectionTitle("Especialização")
        Text(personal.specialty ?: "Não informada")

        Spacer(Modifier.height(12.dp))
        SectionTitle("Experiência")
        Text(personal.experience ?: "Não informada")
    }
}

/**
 * Tela de edição — usa o PersonalViewModel.updatePersonal(...) que monta o DTO e chama a API.
 * Mostra AlertDialog com o resultado e volta só após confirmação.
 */
@Composable
fun PersonalProfileEditScreen(
    onBack: () -> Unit,
    personalId: String,
    personalViewModel: PersonalViewModel = viewModel()
) {
    val state by personalViewModel.uiState.collectAsState()

    // campos editáveis (somente os 3 solicitados)
    var specialization by rememberSaveable { mutableStateOf("") }
    var about by rememberSaveable { mutableStateOf("") }
    var experience by rememberSaveable { mutableStateOf("") }

    // dialog
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    // carrega personal ao abrir
    LaunchedEffect(personalId) {
        personalViewModel.loadPersonal(personalId)
    }

    // preenche campos quando o personal for carregado
    LaunchedEffect(state.personal) {
        state.personal?.let { p ->
            if (specialization.isBlank()) specialization = p.specialty ?: ""
            if (about.isBlank()) about = p.bio ?: ""
            if (experience.isBlank()) experience = p.experience ?: ""
        }
    }

    Scaffold(
        topBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(FitYellow)
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Voltar",
                        tint = FitBlack
                    )
                }

                Text(
                    "Editar Perfil",
                    style = MaterialTheme.typography.titleLarge,
                    color = FitBlack,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            Image(
                painter = painterResource(R.drawable.ic_male),
                contentDescription = null,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(24.dp))

            // Especialização (specialization)
            OutlinedTextField(
                value = specialization,
                onValueChange = { specialization = it },
                label = { Text("Especialização", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving
            )

            Spacer(Modifier.height(16.dp))

            // Sobre / About (aboutMe)
            OutlinedTextField(
                value = about,
                onValueChange = { about = it },
                label = { Text("Sobre (about)", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !state.isSaving
            )

            Spacer(Modifier.height(16.dp))

            // Experiência (experience)
            OutlinedTextField(
                value = experience,
                onValueChange = { experience = it },
                label = { Text("Experiência", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                enabled = !state.isSaving
            )

            Spacer(Modifier.height(32.dp))

            // SALVAR — apenas um botão (antes havia duplicata)
            Button(
                onClick = {
                    if (specialization.isBlank() && about.isBlank() && experience.isBlank()) {
                        dialogMessage = "Preencha ao menos um dos campos antes de salvar."
                        showDialog = true
                        return@Button
                    }

                    personalViewModel.updatePersonal(
                        personalId = personalId,
                        aboutMe = about,
                        specialization = specialization,
                        experience = experience
                    ) { ok, msg ->
                        Log.d("PersonalScreen", "callback received -> ok=$ok msg=$msg")

                        val normalized = (msg ?: "").trim().ifEmpty { if (ok) "Atualizado com sucesso" else "Erro ao atualizar" }

                        dialogMessage = normalized
                        showDialog = true
                        // NÃO chamar onBack aqui — vamos fazer no OK do diálogo
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FitYellow),
                modifier = Modifier
                    .height(45.dp)
                    .fillMaxWidth(),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(color = FitBlack, strokeWidth = 2.dp)
                } else {
                    Text("Salvar", color = FitBlack, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // DIALOG CONFIRMAÇÃO
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = if (dialogMessage.contains("sucesso", ignoreCase = true)) "Sucesso" else "Resultado",
                    color = FitBlack,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = dialogMessage,
                    color = FitBlack,
                    modifier = Modifier.padding(top = 4.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        // só volta se a mensagem indicar sucesso
                        if (dialogMessage.contains("sucesso", ignoreCase = true)) {
                            onBack()
                        }
                    }
                ) {
                    Text("OK", color = FitBlack)
                }
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 6.dp))
}
