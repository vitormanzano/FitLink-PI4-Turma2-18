package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    appViewModel: AppViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val state = profileViewModel.state
    val clientId = appViewModel.clientId

    LaunchedEffect(clientId) {
        if (!clientId.isNullOrBlank() && state.nome.isBlank()) {
            profileViewModel.loadProfile(clientId)
        }
    }

    var nome by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }
    var altura by rememberSaveable { mutableStateOf("") }
    var peso by rememberSaveable { mutableStateOf("") }

    val objetivos = listOf("Hipertrofia", "Emagrecimento", "Condicionamento", "Resistência", "Mobilidade")
    var objetivo by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var objetivoDescricao by rememberSaveable {
        mutableStateOf(state.bio.ifBlank { "Ganhar massa muscular e força com acompanhamento personalizado." })
    }

    var mostrarDialog by remember { mutableStateOf(false) }
    var mensagemDialog by remember { mutableStateOf("") }

    LaunchedEffect(state.nome, state.bio, state.objetivoTag, state.altura, state.peso) {
        if (state.nome.isNotBlank() && nome.isBlank()) nome = state.nome
        if (state.bio.isNotBlank() && bio.isBlank()) bio = state.bio
        if (state.objetivoTag.isNotBlank() && objetivo.isBlank()) objetivo = state.objetivoTag
        if (state.altura.isNotBlank() && altura.isBlank()) altura = state.altura
        if (state.peso.isNotBlank() && peso.isBlank()) peso = state.peso
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
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
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Objetivo",
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(8.dp))

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        objetivo.ifBlank { "Selecione um objetivo" },
                        modifier = Modifier.weight(1f),
                        color = FitBlack
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    objetivos.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                objetivo = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = objetivoDescricao,
                onValueChange = { objetivoDescricao = it },
                label = { Text("Descrição do Objetivo", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = altura,
                    onValueChange = { altura = it },
                    label = { Text("Altura", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {

                    if (objetivo.isBlank() || altura.isBlank() || peso.isBlank()) {
                        mensagemDialog = "Preencha objetivo, altura e peso antes de salvar."
                        mostrarDialog = true
                        return@Button
                    }

                    profileViewModel.salvar(
                        bio = bio,
                        goals = objetivo,
                        altura = altura,
                        peso = peso
                    ) { ok, msg ->
                        mensagemDialog = msg
                        mostrarDialog = true
                        if (ok) onBack()
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FitYellow),
                modifier = Modifier
                    .height(45.dp)
                    .fillMaxWidth()
            ) {
                Text("Salvar", color = FitBlack, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = { mostrarDialog = false },
            confirmButton = {
                TextButton(onClick = { mostrarDialog = false }) {
                    Text("OK", color = FitBlack)
                }
            },
            text = { Text(mensagemDialog) }
        )
    }
}
