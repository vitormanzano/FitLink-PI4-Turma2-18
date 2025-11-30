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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.ui.components.BottomBar
import br.edu.puc.fitlink.ui.theme.FitBlack
import br.edu.puc.fitlink.ui.theme.FitYellow

@Composable
fun UserProfileScreen(
    navController: NavHostController,
    appViewModel: AppViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val state = profileViewModel.state
    val clientId = appViewModel.clientId
    val context = LocalContext.current


    LaunchedEffect(clientId) {
        if (!clientId.isNullOrBlank()) {
            profileViewModel.loadProfile(clientId)
        }
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
            ProfileTopBar(
                onLogout = {
                    appViewModel.logoutClient(context)
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    ) { inner ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
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

            Spacer(Modifier.height(12.dp))

            Text(
                text = state.nome.ifBlank { "Usuário" },
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
                Text(
                    text = state.bio.ifBlank { "Adicione uma descrição sobre você na edição de perfil." },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(24.dp))

                // OBJETIVOS
                SectionTitle("Objetivos")
                Text(
                    text = state.objetivoTag.ifBlank { "Defina seus objetivos na edição de perfil." },
                    fontWeight = FontWeight.ExtraBold
                )
                // Se quiser, você pode tirar essa descrição, já que o back só tem Goals
                Spacer(Modifier.height(6.dp))
                Text(
                    text = state.objetivoTag.ifBlank { "" },
                    style = MaterialTheme.typography.bodyMedium
                )
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
                        Text(
                            text = if (state.altura.isNotBlank()) "${state.altura} m" else "-",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Column {
                        Text("Peso", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (state.peso.isNotBlank()) "${state.peso} kg" else "-",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Se quiser exibir erro embaixo
                state.error?.let { msg ->
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
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
            Icon(
                Icons.AutoMirrored.Outlined.Logout,
                contentDescription = "Logout",
                tint = FitBlack
            )
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
