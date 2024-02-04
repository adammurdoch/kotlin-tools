package net.rubygrapefruit.process

actual fun pwd(): List<String> {
    return listOf("pwd")
}

actual fun ls(): List<String> {
    return listOf("ls")
}