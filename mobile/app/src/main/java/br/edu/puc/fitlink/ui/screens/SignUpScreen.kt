package br.edu.puc.fitlink.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.draw.clip
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
    var tipoMensagem by remember { mutableStateOf("info") }
    var carregando by remember { mutableStateOf(false) }

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
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.Black)
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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_fitlink),
                    contentDescription = "Logo FitLink",
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "FitLink",
                    fontSize = 52.sp,
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

            InputField("Nome", nome, { nome = it }, Icons.Default.Person)
            Spacer(modifier = Modifier.height(16.dp))

            InputField("Telefone", telefone, { telefone = it }, Icons.Default.Phone, KeyboardType.Phone)
            Spacer(modifier = Modifier.height(16.dp))

            InputField("Email", email, { email = it }, Icons.Default.Email, KeyboardType.Email)
            Spacer(modifier = Modifier.height(16.dp))

            if (isProfessor) {
                InputField("CREF", cref, { cref = it }, Icons.Default.Badge)
                Spacer(modifier = Modifier.height(16.dp))
                InputField("CPF", cpf, { cpf = it }, Icons.Default.CreditCard, KeyboardType.Number)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ===== SENHA =====
            TextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Aluno",
                    fontSize = 18.sp,
                    fontWeight = if (!isProfessor) FontWeight.Bold else FontWeight.Normal,
                    color = if (!isProfessor) Color.Black else Color.Gray
                )

                Switch(
                    checked = isProfessor,
                    onCheckedChange = { isProfessor = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Black,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Black
                    )
                )

                Text(
                    "Professor",
                    fontSize = 18.sp,
                    fontWeight = if (isProfessor) FontWeight.Bold else FontWeight.Normal,
                    color = if (isProfessor) Color.Black else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== BOTÃO CADASTRAR =====
            Button(
                onClick = {
                    scope.launch {
                        carregando = true

                        if (isProfessor) {
                            // ====== PERSONAL ======
                            socketVm.validarSignUpPersonal(
                                name = nome,
                                email = email,
                                password = senha,
                                phone = telefone,
                                city = "cidadeteste", // depois troca pelo campo de cidade real
                                cpf = cpf,
                                cref = cref
                            ) { valido, msgErro ->
                                if (valido) {
                                    tipoMensagem = "success"
                                    mensagemDialog = "Dados válidos! Cadastrando personal..."
                                    mostrarDialog = true

                                    scope.launch {
                                        delay(1000)

                                        val dto = RegisterPersonalDto(
                                            name = nome,
                                            email = email,
                                            password = senha,
                                            city = "cidadeteste",
                                            cpf = cpf,
                                            cref = cref,
                                            phone = telefone
                                        )

                                        authVm.registerPersonal(dto) { ok, msg ->
                                            carregando = false
                                            tipoMensagem = if (ok) "success" else "error"
                                            mensagemDialog = if (ok)
                                                "Cadastro realizado com sucesso!"
                                            else
                                                "Erro: $msg"
                                            mostrarDialog = true

                                            scope.launch {
                                                delay(1500)
                                                if (ok) navController.navigate("login")
                                            }
                                        }
                                    }
                                } else {
                                    carregando = false
                                    tipoMensagem = "error"
                                    mensagemDialog = msgErro ?: "Dados inválidos! Verifique os campos."
                                    mostrarDialog = true
                                }
                            }
                        } else {
                            // ====== CLIENTE ======
                            socketVm.validarSignUpClient(
                                name = nome,
                                email = email,
                                password = senha,
                                phone = telefone,
                                city = "cidadeteste" // depois troca pelo campo real
                            ) { valido, msgErro ->
                                if (valido) {
                                    tipoMensagem = "success"
                                    mensagemDialog = "Dados válidos! Cadastrando..."
                                    mostrarDialog = true

                                    scope.launch {
                                        delay(1000)

                                        val dto = RegisterClientDto(
                                            name = nome,
                                            email = email,
                                            password = senha,
                                            phone = telefone,
                                            city = "cidadeteste"
                                        )

                                        authVm.register(dto) { ok, msg ->
                                            carregando = false
                                            tipoMensagem = if (ok) "success" else "error"
                                            mensagemDialog = if (ok)
                                                "Cadastro realizado com sucesso!"
                                            else
                                                "Erro: $msg"
                                            mostrarDialog = true

                                            scope.launch {
                                                delay(1500)
                                                if (ok) navController.navigate("login")
                                            }
                                        }
                                    }
                                } else {
                                    carregando = false
                                    tipoMensagem = "error"
                                    mensagemDialog = msgErro ?: "Dados inválidos! Verifique os campos."
                                    mostrarDialog = true
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                if (carregando)
                    CircularProgressIndicator(
                        modifier = Modifier.size(26.dp),
                        color = Color.Black,
                        strokeWidth = 3.dp
                    )
                else
                    Text("Cadastre-se", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold)
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

    // ===== DIALOG BONITO =====
    AnimatedVisibility(
        visible = mostrarDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0x66000000))
                .wrapContentSize(Alignment.Center)
        ) {
            Card(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .padding(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (tipoMensagem) {
                        "success" -> Color(0xFFDFFFD6)
                        "error" -> Color(0xFFFFD6D6)
                        else -> Color(0xFFFFF6CC)
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = when (tipoMensagem) {
                            "success" -> Icons.Default.CheckCircle
                            "error" -> Icons.Default.Error
                            else -> Icons.Default.Info
                        },
                        contentDescription = null,
                        tint = when (tipoMensagem) {
                            "success" -> Color(0xFF2E7D32)
                            "error" -> Color(0xFFC62828)
                            else -> Color(0xFFB28704)
                        },
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = mensagemDialog,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { mostrarDialog = false }) {
                        Text("OK", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ===== COMPONENTES AUX =====
@Composable
private fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        colors = underlineColors()
    )
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
