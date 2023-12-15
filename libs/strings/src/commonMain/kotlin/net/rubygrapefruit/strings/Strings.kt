package net.rubygrapefruit.strings

fun String.capitalized(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}