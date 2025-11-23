package br.edu.puc.fitlink.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import br.edu.puc.fitlink.utils.SpCities

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdownField(
    value: String,                      // cidade selecionada (oficial)
    onValueChange: (String) -> Unit,    // só muda quando o usuário escolhe da lista
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    // Lista filtrada pelo que o usuário digitar no campo de busca
    val filteredCities = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            SpCities.list
        } else {
            SpCities.list.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        // Campo PRINCIPAL: apenas mostra a cidade selecionada, não edita texto aqui
        TextField(
            value = value,
            onValueChange = { /* não deixa editar aqui */ },
            readOnly = true,
            label = { Text(label) },
            singleLine = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White,   // fundo branco
                unfocusedContainerColor = Color.White, // fundo branco
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Black
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                // quando fecha o menu, reseta a busca pra não ficar lixo de texto
                searchQuery = ""
                focusManager.clearFocus()
            }
        ) {
            // Campo de busca DENTRO do dropdown
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar cidade") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color.Black
                )
            )

            Divider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp) // limita altura do menu
                    .verticalScroll(rememberScrollState())
            ) {
                if (filteredCities.isEmpty()) {
                    Text(
                        text = "Nenhuma cidade encontrada",
                        modifier = Modifier.padding(12.dp),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    filteredCities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                onValueChange(city)   // aqui muda o valor oficial
                                expanded = false
                                searchQuery = ""
                                focusManager.clearFocus()
                            }
                        )
                    }
                }
            }
        }
    }
}
