package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.auth.AuthViewModel
import br.edu.puc.fitlink.data.model.LoginClientDto
import br.edu.puc.fitlink.data.model.LoginPersonalDto
import br.edu.puc.fitlink.validations.ClienteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController, onLoginSuccess: (String, Boolean) -> Unit) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var isProfessor by remember { mutableStateOf(false) }

    var mostrarDialog by remember { mutableStateOf(false) }
    var mensagemDialog by remember { mutableStateOf("") }

    val socketVm = remember { ClienteViewModel() }
    val apiVm: AuthViewModel = viewModel()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ===== TOPO =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFC107))
                .padding(top = 22.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.Black
                )
            }
        }

        // ===== LOGO =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFC107))
                .padding(top = 40.dp, bottom = 48.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_fitlink),
                    contentDescription = "Logo FitLink",
                    modifier = Modifier
                        .height(120.dp)
                        .width(120.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "FitLink",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // ===== CAMPOS =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Email
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Senha
            TextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Icon(
                            imageVector = if (senhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Ver senha"
                        )
                    }
                },
                visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { /* TODO: recuperar senha */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Esqueci a senha", color = Color.Black, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Switch aluno/professor =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aluno",
                    fontSize = 18.sp,
                    color = if (!isProfessor) Color.Black else Color.Gray,
                    fontWeight = if (!isProfessor) FontWeight.Bold else FontWeight.Normal
                )

                Switch(
                    checked = isProfessor,
                    onCheckedChange = { isProfessor = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        uncheckedThumbColor = Color.White,
                        checkedTrackColor = Color.Black,
                        uncheckedTrackColor = Color.Black
                    )
                )

                Text(
                    text = "Professor",
                    fontSize = 18.sp,
                    color = if (isProfessor) Color.Black else Color.Gray,
                    fontWeight = if (isProfessor) FontWeight.Bold else FontWeight.Normal
                )
            }

            // ===== BOTÃO LOGIN =====
            Button(
                onClick = {
                    scope.launch {
                        socketVm.validarEmail(email) { valido ->
                            if (valido) {
                                mensagemDialog = "E-mail válido! Entrando..."
                                mostrarDialog = true

                                scope.launch {
                                    delay(1000)

                                    if (isProfessor) {
                                        // PROFESSOR
                                        apiVm.loginPersonal(LoginPersonalDto(email, senha)) { ok, user, erro ->
                                            if (ok && user != null) {
                                                mensagemDialog = "Bem-vindo, ${user.name}!"
                                                mostrarDialog = true
                                                onLoginSuccess(user.id, true)
                                            } else {
                                                mensagemDialog = erro ?: "Falha no login."
                                                mostrarDialog = true
                                            }
                                        }
                                    } else {
                                        // ALUNO
                                        apiVm.login(LoginClientDto(email, senha)) { ok, user, erro ->
                                            if (ok && user != null) {
                                                mensagemDialog = "Bem-vindo, ${user.name}!"
                                                mostrarDialog = true
                                                onLoginSuccess(user.id, false)
                                            } else {
                                                mensagemDialog = erro ?: "Falha no login."
                                                mostrarDialog = true
                                            }
                                        }
                                    }
                                }
                            } else {
                                mensagemDialog = "E-mail inválido!"
                                mostrarDialog = true
                            }
                        }
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Entrar", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== SEPARADOR =====
            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.weight(1f))
                Text("  ou  ", fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Center)
                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== BOTÃO CADASTRO =====
            OutlinedButton(
                onClick = { navController.navigate("signUp") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text(
                    "Cadastre-se",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // ===== DIALOG =====
    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = { mostrarDialog = false },
            confirmButton = {
                TextButton(onClick = { mostrarDialog = false }) {
                    Text("OK", color = Color.Black)
                }
            },
            text = { Text(mensagemDialog, fontSize = 16.sp) }
        )
    }
}
