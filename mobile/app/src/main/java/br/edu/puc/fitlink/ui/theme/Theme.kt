package br.edu.puc.fitlink.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val FitYellow = Color(0xFFFFC107) // barra topo/aba inferior
val FitRed = Color(0xFFE2554F)    // botão "Procurar um Personal"
val FitBlack = Color(0xFF111111)
val FitGray = Color(0xFF777777)
val FitBg = Color(0xFFFFFFFF)

private val colorScheme = lightColorScheme(
    primary = FitYellow,
    secondary = FitYellow,
    onPrimary = FitBlack,
    background = FitBg,
    surface = FitBg,
    onBackground = FitBlack,
    onSurface = FitBlack
)

@Composable
fun FitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}