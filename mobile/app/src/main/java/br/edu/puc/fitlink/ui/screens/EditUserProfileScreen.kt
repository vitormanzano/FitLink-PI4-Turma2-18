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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.ui.components.CircleAvatarPlaceholder
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow

@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    // Dados fixos para teste
    var nome by remember { mutableStateOf("Gabriel Adorno") }
    var bio by remember { mutableStateOf("Sou estudante de Engenharia de Software e apaixonado por academia e tecnologia.") }
    var altura by remember { mutableStateOf("1.83") }
    var peso by remember { mutableStateOf("80.0") }

    val objetivos = listOf("Hipertrofia", "Emagrecimento", "Condicionamento", "Resistência", "Mobilidade")
    var objetivo by remember { mutableStateOf("Hipertrofia") }
    var expanded by remember { mutableStateOf(false) }
    var objetivoDescricao by remember { mutableStateOf("Ganhar massa muscular e força com acompanhamento personalizado.") }

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
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Voltar", tint = FitBlack)
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

            // Avatar fixo (ic_male)
            Image(
                painter = painterResource(R.drawable.ic_male),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(24.dp))

            // Campos de texto
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

            // Dropdown
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
                    Text(objetivo, modifier = Modifier.weight(1f), color = FitBlack)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    objetivos.forEach {
                        DropdownMenuItem(
                            text = { Text(it, color = FitBlack) },
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
                onClick = { /* ação de salvar */ },
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
}