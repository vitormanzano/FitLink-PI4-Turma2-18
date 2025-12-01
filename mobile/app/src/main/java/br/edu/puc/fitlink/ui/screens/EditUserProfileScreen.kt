package br.edu.puc.fitlink.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow
import kotlinx.coroutines.delay

// ---------- Helpers para altura ----------
/**
 * Recebe apenas dígitos (ex: "180") e retorna string formatada com vírgula (ex: "1,80")
 */
fun formatarAlturaDeDigitos(digitos: String): String {
    val d = digitos.filter { it.isDigit() }
    if (d.isEmpty()) return ""

    return when (d.length) {
        1 -> "0,0$d"           // "1" -> "0,01"
        2 -> "0,$d"            // "18" -> "0,18"
        3 -> "${d[0]},${d.substring(1)}" // "180" -> "1,80"
        else -> "${d.dropLast(2)},${d.takeLast(2)}" // "1800" -> "18,00"
    }
}

/**
 * Converte dígitos para string em metros com ponto (para salvar, ex: "1.80").
 * Se digitos vazio retorna "".
 */
fun alturaParaSalvarEmFloatString(digitos: String): String {
    val d = digitos.filter { it.isDigit() }
    if (d.isEmpty()) return ""
    return when (d.length) {
        1 -> "0.0${d}"
        2 -> "0.${d}"
        else -> "${d.dropLast(2)}.${d.takeLast(2)}"
    }
}

/**
 * Tenta extrair dígitos a partir de uma string inicial (p.ex. "1,80" ou "1.80" ou "180")
 */
fun extrairDigitosDeAlturaInicial(valor: String): String {
    // Remove tudo que não é dígito
    return valor.filter { it.isDigit() }
}

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

    // agora guardamos os dígitos separadamente e mostramos a versão formatada
    var alturaDigits by rememberSaveable { mutableStateOf("") }      // ex: "180"
    var alturaDisplay by remember { mutableStateOf("") }            // ex: "1,80"

    var peso by rememberSaveable { mutableStateOf("") }

    val objetivos = listOf("Hipertrofia", "Emagrecimento", "Condicionamento", "Resistência", "Mobilidade", "Outro")
    var objetivo by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var mostrarDialog by remember { mutableStateOf(false) }
    var mensagemDialog by remember { mutableStateOf("") }

    // quando carregar estado do servidor, inicializa campos
    LaunchedEffect(state.nome, state.bio, state.objetivoTag, state.altura, state.peso) {
        if (state.nome.isNotBlank() && nome.isBlank()) nome = state.nome
        if (state.bio.isNotBlank() && bio.isBlank()) bio = state.bio
        if (state.objetivoTag.isNotBlank() && objetivo.isBlank()) objetivo = state.objetivoTag
        if (state.altura.isNotBlank() && alturaDisplay.isBlank()) {
            // state.altura pode estar "1.80" ou "1,80" ou "180"
            val digits = extrairDigitosDeAlturaInicial(state.altura)
            alturaDigits = digits
            alturaDisplay = formatarAlturaDeDigitos(digits)
        }
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
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ------------------------
                // ALTURA FORMATADA: usamos ALTURA-DIGITS como fonte da verdade
                // ------------------------
                OutlinedTextField(
                    value = alturaDisplay,
                    onValueChange = { novo ->
                        // pega apenas os dígitos do que o usuário digitou
                        val apenasDigitos = novo.filter { it.isDigit() }
                        // limitar a 4 dígitos (ex: 2500 -> 25,00 m)
                        val limited = if (apenasDigitos.length > 4) apenasDigitos.take(4) else apenasDigitos

                        alturaDigits = limited
                        alturaDisplay = formatarAlturaDeDigitos(limited)
                    },
                    label = { Text("Altura (m)", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it.filter { ch -> ch.isDigit() || ch == ',' || ch == '.' } },
                    label = { Text("Peso (kg)", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {

                    if (objetivo.isBlank() || alturaDigits.isBlank() || peso.isBlank()) {
                        mensagemDialog = "Preencha objetivo, altura e peso antes de salvar."
                        mostrarDialog = true
                        return@Button
                    }

                    // converte para string com ponto para salvar (ex: "1.80")
                    val alturaParaSalvar = alturaParaSalvarEmFloatString(alturaDigits)

                    profileViewModel.salvar(

                        bio = bio,
                        goals = objetivo,
                        altura = alturaParaSalvar, // envia "1.80"
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
        AnimatedVisibility(
            visible = mostrarDialog,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {

            // Controla o tempo que o dialog fica na tela
            LaunchedEffect(Unit) {
                delay(1500)   // tempo visível real
                mostrarDialog = false
            }

            CustomDialog(
                mensagem = "Informações salvas com sucesso!",
                icon = Icons.Default.CheckCircle,
                iconColor = FitYellow,
                showLoading = false,
                onDismiss = { mostrarDialog = false }
            )
        }

    }


}
