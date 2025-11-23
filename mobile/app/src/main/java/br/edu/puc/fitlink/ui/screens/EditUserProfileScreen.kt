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
import br.edu.puc.fitlink.ui.components.CircleAvatarPlaceholder
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow

@Composable
fun EditProfileScreen(
    avatarRes: Int? = null,
    nomeInicial: String,
    bioInicial: String,
    objetivoInicial: String,
    objetivoDescricaoInicial: String,
    alturaInicial: Double,
    pesoInicial: Double,

    onSave: (
        nome: String,
        bio: String,
        objetivo: String,
        objetivoDescricao: String,
        altura: Double,
        peso: Double
    ) -> Unit,
    onBack: () -> Unit
) {
    // Estados locais editáveis
    var nome by remember { mutableStateOf(nomeInicial) }
    var bio by remember { mutableStateOf(bioInicial) }
    var altura by remember { mutableStateOf(alturaInicial.toString()) }
    var peso by remember { mutableStateOf(pesoInicial.toString()) }

    // Dropdown estados
    val objetivos = listOf("Hipertrofia", "Emagrecimento", "Condicionamento", "Resistência", "Mobilidade")
    var objetivo by remember { mutableStateOf(objetivoInicial) }
    var expanded by remember { mutableStateOf(false) }

    var objetivoDescricao by remember { mutableStateOf(objetivoDescricaoInicial) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(FitYellow)
            ) {
                IconButton(
                    onClick = onBack,
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

            // Avatar
            if (avatarRes != null) {
                Image(
                    painter = painterResource(avatarRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                )
            } else {
                CircleAvatarPlaceholder()
            }

            Spacer(Modifier.height(24.dp))

            // CAMPOS DE TEXTO
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = {
                    Text(
                        text = "Nome",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = {
                    Text(
                        text = "Bio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(24.dp))

            // DROPDOWN DO OBJETIVO
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
                label = {
                    Text(
                        text = "Descrição do Objetivo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(24.dp))

            // Medidas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = altura,
                    onValueChange = { altura = it },
                    label = {
                        Text(
                            text = "Altura",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = {
                        Text(
                            text = "Peso",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    onSave(
                        nome,
                        bio,
                        objetivo,
                        objetivoDescricao,
                        altura.toDoubleOrNull() ?: 0.0,
                        peso.toDoubleOrNull() ?: 0.0
                    )
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
}


@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    MaterialTheme {
        EditProfileScreen(
            avatarRes = null,          // ou algum drawable, ex: R.drawable.user
            nomeInicial = "Biel",
            bioInicial = "Sou o Biel e gosto de academia.",
            objetivoInicial = "Hipertrofia",
            objetivoDescricaoInicial = "Aumentar massa muscular com treino intenso.",
            alturaInicial = 1.83,
            pesoInicial = 80.0,
            onSave = { _, _, _, _, _, _ -> },
            onBack = {}
        )
    }
}
