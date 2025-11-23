package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Whatsapp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.ui.components.BottomBar
import br.edu.puc.fitlink.ui.components.BottomBarPersonal
import br.edu.puc.fitlink.ui.components.TopBar
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow

@Composable
fun StudentsDetailsScreen(navController: NavHostController) {
    var isAluno = false;

    val nome = "Gabriel Adorno"
    val bio = "Sou aluno dedicado, focado em melhorar minha saúde e desempenho físico. Tenho como meta alcançar a melhor versão de mim mesmo."
    val objetivoTag = "Hipertrofia"
    val objetivoDescricao = "Meu foco atual é o ganho de massa muscular, com treinos regulares e acompanhamento nutricional."
    val altura = 1.83
    val peso = 80.0

    Scaffold(
        topBar = {
            TopBar(title = "Perfil do Aluno", showBack = true, onBack = { navController.popBackStack() })
        }
    ) { inner ->

        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            // Imagem fixa do aluno
            Image(
                painter = painterResource(R.drawable.ic_male),
                contentDescription = "Foto do aluno",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(12.dp))

            // Nome do aluno
            Text(
                text = nome,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(16.dp))

            if (!isAluno) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Button(
                        onClick = { /* entrar em contato com personal */ },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FitYellow),
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center, // centraliza horizontalmente
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Whatsapp,
                                contentDescription = null,
                                tint = FitBlack
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // espaço entre ícone e texto
                            Text(
                                "Entrar em Contato",
                                color = FitBlack,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }


                    OutlinedButton(
                        onClick = { navController.navigate("studentsWorkout")},
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
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { /* entrar em contato com personal */ },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FitYellow),
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center, // centraliza horizontalmente
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Whatsapp,
                                contentDescription = null,
                                tint = FitBlack
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // espaço entre ícone e texto
                            Text(
                                "Entrar em Contato",
                                color = FitBlack,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { /* ação adicionar */ },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // verde
                            modifier = Modifier
                                .height(40.dp)
                                .weight(1f)
                        ) {
                            Text("Adicionar", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { /* ação recusar */ },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)), // vermelho
                            modifier = Modifier
                                .height(40.dp)
                                .weight(1f)
                        ) {
                            Text("Recusar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // 📄 Seções de informações
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                SectionTitle("Sobre")
                Text(bio, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(24.dp))

                SectionTitle("Objetivos")
                Text(objetivoTag, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(6.dp))
                Text(objetivoDescricao, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(24.dp))

                SectionTitle("Medidas")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column {
                        Text("Altura", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("${altura}m", style = MaterialTheme.typography.bodyMedium)
                    }

                    Column {
                        Text("Peso", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("${peso}kg", style = MaterialTheme.typography.bodyMedium)
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
