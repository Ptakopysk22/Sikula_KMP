package cz.bosan.sikula_kmp.managers.camp_manager.presentation

import androidx.compose.ui.graphics.Color

fun hexToColor(hex: String): Color {
    try {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16).toInt()
        return if (cleanHex.length == 6) {
            Color(
                red = ((colorInt shr 16) and 0xFF) / 255f,
                green = ((colorInt shr 8) and 0xFF) / 255f,
                blue = (colorInt and 0xFF) / 255f,
                alpha = 1f
            )
        } else if (cleanHex.length == 8) {
            Color(
                red = ((colorInt shr 16) and 0xFF) / 255f,
                green = ((colorInt shr 8) and 0xFF) / 255f,
                blue = (colorInt and 0xFF) / 255f,
                alpha = ((colorInt shr 24) and 0xFF) / 255f
            )
        } else {
            Color.Black
        }
    } catch (e: Exception) {
        return Color.Black
    }
}

fun colorToHex(color: Color): String {
    val red = (color.red * 255).toInt().coerceIn(0, 255)
    val green = (color.green * 255).toInt().coerceIn(0, 255)
    val blue = (color.blue * 255).toInt().coerceIn(0, 255)
    val alpha = (color.alpha * 255).toInt().coerceIn(0, 255)

    return if (alpha != 255) {
        "#" + alpha.toHex() + red.toHex() + green.toHex() + blue.toHex()
    } else {
        "#" + red.toHex() + green.toHex() + blue.toHex()
    }
}

private fun Int.toHex(): String {
    val hex = this.toString(16).uppercase()
    return if (hex.length == 1) "0$hex" else hex
}