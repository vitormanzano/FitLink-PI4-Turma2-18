package br.edu.puc.fitlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.ui.components.TopBar
import br.edu.puc.fitlink.ui.theme.FitBlack

data class Personal(
    val id: Int,
    val nome: String,
    val cidade: String,
    val fotoRes: Int
)

@Composable
fun SearchAScreen(
    navController: NavHostController,
    onPersonalClick: (Personal) -> Unit = {}
) {
    var searchCidade by remember { mutableStateOf("") }
    var searchNome by remember { mutableStateOf("") }

    val personais = listOf(
        Personal(1,"Carlos Silva",  "Campinas", R.drawable.ic_male),
        Personal(2,"Ana Souza", "Campinas", R.drawable.ic_female),
        Personal(3,"Lucas Mendes", "São Paulo", R.drawable.ic_male),
        Personal(4,"Mariana Torres", "Valinhos", R.drawable.ic_female),
        Personal(5,"Rafael Costa", "São Paulo", R.drawable.ic_male)
    )

    // --- FILTRAGEM ---
    val personaisFiltrados = personais.filter { p ->
        val cidadeOk = if (searchCidade.isBlank()) true
        else p.cidade.contains(searchCidade, ignoreCase = true)

        val nomeOk = if (searchNome.isBlank()) true
        else p.nome.contains(searchNome, ignoreCase = true)

        cidadeOk && nomeOk
    }

    Column(Modifier.fillMaxSize()) {
        TopBar(title = "Encontre seu Personal")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))

                // Campo de busca - Cidade
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    TextField(
                        value = searchCidade,
                        onValueChange = { searchCidade = it },
                        placeholder = { Text("Digite a cidade") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Place, contentDescription = null)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    TextField(
                        value = searchNome,
                        onValueChange = { searchNome = it },
                        placeholder = { Text("Digite o nome do personal") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Person, contentDescription = null)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(24.dp))
            }

            // Lista de personais filtrados
            if (personaisFiltrados.isEmpty()) {
                item {
                    Text(
                        "Nenhum personal encontrado.",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
            } else {
                items(personaisFiltrados) { p ->
                    PersonalItem(p) { navController.navigate("personalDetail") }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun PersonalItem(personal: Personal, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(personal.fotoRes),
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                personal.nome,
                fontWeight = FontWeight.Bold,
                color = FitBlack
            )
            Text(
                personal.cidade,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
