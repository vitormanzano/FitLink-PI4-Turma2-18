package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.ui.components.BottomBar
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow

@Composable
fun UserProfileScreen(navController: NavHostController) {
    val nome = "Gabriel Adorno"
    val bio = "Sou estudante de Engenharia de Software e apaixonado por academia. Gosto de treinar e desenvolver projetos que unem tecnologia e saúde."
    val objetivoTag = "Hipertrofia"
    val objetivoDescricao = "Plano voltado ao ganho de massa muscular, com treinos semanais e acompanhamento personalizado."
    val altura = 1.83
    val peso = 80.0

    Scaffold(
        bottomBar = { BottomBar(current = "profile", onNavigate = {}) },
        topBar = { ProfileTopBar(onLogout = {}) }
    ) { inner ->

        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            // Avatar fixo com ic_male
            Image(
                painter = painterResource(R.drawable.ic_male),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(12.dp))

            // Nome
            Text(
                text = nome,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(16.dp))

            EditProfileButton(onClick = { navController.navigate("editProfile") })

            Spacer(Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // SOBRE
                SectionTitle("Sobre")
                Text(bio, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(24.dp))

                // OBJETIVOS
                SectionTitle("Objetivos")
                Text(objetivoTag, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(6.dp))
                Text(objetivoDescricao, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(24.dp))

                // MEDIDAS
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
private fun ProfileTopBar(onLogout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(FitYellow)
    ) {
        Text(
            "Perfil",
            style = MaterialTheme.typography.titleLarge,
            color = FitBlack,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = onLogout,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
        ) {
            Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Logout", tint = FitBlack)
        }
    }
}

@Composable
private fun EditProfileButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FitYellow),
        modifier = Modifier.height(40.dp)
    ) {
        Text("Editar Perfil", color = FitBlack, fontWeight = FontWeight.SemiBold)
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

