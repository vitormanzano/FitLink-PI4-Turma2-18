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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.data.model.PersonalResponseDto
import br.edu.puc.fitlink.ui.components.TopBar
import br.edu.puc.fitlink.ui.theme.FitBlack

@Composable
fun SearchAScreen(
    onPersonalClick: (PersonalResponseDto) -> Unit = {}
) {
    val vm: SearchViewModel = viewModel()

    var searchCidade by remember { mutableStateOf("") }
    var searchNome by remember { mutableStateOf("") }

    LaunchedEffect(searchCidade) {
        if (searchCidade.length >= 3) {
            vm.searchByCity(searchCidade)
        } else {
            vm.searchByCity("") // limpa resultados se apagar a cidade
        }
    }

    val personais = vm.searchResults.filter { p ->
        p.name.contains(searchNome, ignoreCase = true)
    }

    Column(Modifier.fillMaxSize()) {

        TopBar(title = "Encontre seu Personal")

        Spacer(Modifier.height(16.dp))

        TextField(
            value = searchCidade,
            onValueChange = { searchCidade = it },
            placeholder = { Text("Digite a cidade") },
            leadingIcon = { Icon(Icons.Outlined.Place, null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        TextField(
            value = searchNome,
            onValueChange = { searchNome = it },
            placeholder = { Text("Digite o nome do personal") },
            leadingIcon = { Icon(Icons.Outlined.Person, null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(16.dp))

        when {
            vm.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            vm.errorMessage != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(vm.errorMessage!!, color = Color.Red)
                }
            }

            else -> {
                LazyColumn(Modifier.fillMaxSize()) {
                    if (personais.isEmpty()) {
                        item {
                            Text(
                                "Nenhum personal encontrado.",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray
                            )
                        }
                    } else {
                        items(personais) { p ->
                            PersonalItem(p) { onPersonalClick(p) }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalItem(
    personal: PersonalResponseDto,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { onClick() }
    ) {

        // Placeholder de imagem (já que não temos avatar real ainda)
        Image(
            painter = painterResource(R.drawable.ic_male),
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                personal.name,
                fontWeight = FontWeight.Bold,
                color = FitBlack
            )
            Text(
                personal.specialty ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                personal.city ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}