package net.rubygrapefruit.parse.text

internal fun format(char: Char): String {
    return format(char.toString())
}

internal fun format(text: String): String {
    return when {
        text == "\n" -> "new line"
        else -> "\"$text\""
    }
}