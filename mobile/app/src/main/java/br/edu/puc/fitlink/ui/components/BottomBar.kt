package br.edu.puc.fitlink.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomBar(current: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFFFFC107)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarItemCustom(
                selected = current == "home",
                icon = Icons.Outlined.FitnessCenter,
                label = "Treinos",
                onClick = { onNavigate("home") }
            )
            BottomBarItemCustom(
                selected = current == "search",
                icon = Icons.Outlined.Search,
                label = "Busca",
                onClick = { onNavigate("search") }
            )
            BottomBarItemCustom(
                selected = current == "profile",
                icon = Icons.Outlined.Person,
                label = "Perfil",
                onClick = { onNavigate("profile") }
            )
        }
    }
}

@Composable
fun BottomBarPersonal(current: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFFFFC107)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarItemCustom(
                selected = current == "newStudents",
                icon = Icons.Outlined.PersonPin,
                label = "Interessados",
                onClick = { onNavigate("newStudents") }
            )
            BottomBarItemCustom(
                selected = current == "myStudents",
                icon = Icons.Outlined.School,
                label = "Meus alunos",
                onClick = { onNavigate("myStudents") }
            )
            BottomBarItemCustom(
                selected = current == "personalProfile",
                icon = Icons.Outlined.Person,
                label = "Perfil",
                onClick = { onNavigate("personalProfile") }
            )
        }
    }
}

@Composable
fun BottomBarItemCustom(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (selected) 1.15f else 1f, label = "")

    Column(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .width(90.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Círculo de destaque
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (selected) Color(0xFFFFD54F) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = Color.Black,
                modifier = Modifier.scale(scale)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = if (selected) Color.Black else Color.Black.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

