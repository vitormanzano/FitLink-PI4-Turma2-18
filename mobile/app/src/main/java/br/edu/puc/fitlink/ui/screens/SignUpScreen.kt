package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
import br.edu.puc.fitlink.data.model.RegisterClientDto
import br.edu.puc.fitlink.data.model.RegisterPersonalDto
import br.edu.puc.fitlink.validations.ClienteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavHostController) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cref by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var isProfessor by remember { mutableStateOf(false) }

    var mostrarDialog by remember { mutableStateOf(false) }
    var mensagemDialog by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val authVm: AuthViewModel = viewModel()
    val socketVm = remember { ClienteViewModel() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
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
                modifier = Modifier.align(Alignment.Center),
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
            Spacer(modifier = Modifier.height(16.dp))

            if (!isProfessor) {
                // --- ALUNO ---
                TextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = underlineColors()
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = telefone,
                    onValueChange = { telefone = it },
                    label = { Text("Telefone") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = underlineColors()
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    trailingIcon = {
                        if (email.isNotEmpty()) Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = underlineColors()
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                // --- PROFESSOR ---
                TextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = underlineColors()
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = telefone,
                    onValueChange = { telefone = it },
                    label = { Text("Telefone") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = underlineColors()
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    trailingIcon = {
                        if (email.isNotEmpty()) Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = underlineColors()
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = cref,
                    onValueChange = { cref = it },
                    label = { Text("CREF") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = underlineColors()
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = cpf,
                    onValueChange = { cpf = it },
                    label = { Text("CPF") },
                    leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = underlineColors()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ===== SENHA =====
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
                colors = underlineColors()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== SWITCH =====
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

            Spacer(modifier = Modifier.height(16.dp))

            // ===== BOTÃO CADASTRAR =====
            Button(
                onClick = {
                    scope.launch {
                        // 1️⃣ Valida o e-mail via socket antes do cadastro
                        socketVm.validarEmail(email) { valido ->
                            if (valido) {
                                mensagemDialog = "E-mail válido! Cadastrando..."
                                mostrarDialog = true

                                // 2️⃣ Pequeno delay pra mostrar o feedback na UI
                                scope.launch {
                                    delay(1000)

                                    if (isProfessor) {
                                        val dto = RegisterPersonalDto(
                                            name = nome,
                                            email = email,
                                            password = senha,
                                            city = "cidadeteste",
                                            cpf = cpf,
                                            cref = cref,
                                            phone = telefone // ✅ telefone também no professor
                                        )

                                        // 3️⃣ Chama a API de cadastro de personal
                                        authVm.registerPersonal(dto) { ok, msg ->
                                            mensagemDialog = msg
                                            mostrarDialog = true
                                            if (ok) navController.navigate("login")
                                        }
                                    } else {
                                        val dto = RegisterClientDto(
                                            name = nome,
                                            email = email,
                                            password = senha,
                                            phone = telefone,
                                            city = "cidadeteste"
                                        )

                                        // 3️⃣ Chama a API de cadastro de cliente
                                        authVm.register(dto) { ok, msg ->
                                            mensagemDialog = msg
                                            mostrarDialog = true
                                            if (ok) navController.navigate("login")
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
                Text(
                    "Cadastre-se",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== SEPARADOR =====
            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.weight(1f))
                Text("  ou  ", fontSize = 14.sp, color = Color.Black)
                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== BOTÃO ENTRAR =====
            OutlinedButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text("Entre", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
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

@Composable
private fun underlineColors() = TextFieldDefaults.colors(
    focusedIndicatorColor = Color.Black,
    unfocusedIndicatorColor = Color.Gray,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedLabelColor = Color.Black,
    unfocusedLabelColor = Color.Gray,
    cursorColor = Color.Black
)
