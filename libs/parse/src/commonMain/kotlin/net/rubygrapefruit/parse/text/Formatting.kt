package net.rubygrapefruit.parse.text

internal fun format(char: Char): String {
    return "\"$char\""
}

internal fun format(text: String): String {
    return "\"$text\""
}