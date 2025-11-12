package br.edu.puc.fitlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.puc.fitlink.ui.theme.*

@Composable
fun TopBar(title: String, showBack: Boolean = false, onBack: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(FitYellow),
        contentAlignment = Alignment.Center
    ) {
        if (showBack && onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Voltar", tint = FitBlack)
            }
        }
        Text(title, color = FitBlack, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    }
}

data class BottomItem(val route: String, val label: String, val icon: @Composable () -> Unit)

@Composable
fun PrimaryPillButton(text: String, onClick: () -> Unit, bg: Color = FitRed) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun IconCard(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun CircleAvatarPlaceholder() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color(0xFFEFEFEF)),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Outlined.Person, contentDescription = null, tint = FitGray)
    }
}
