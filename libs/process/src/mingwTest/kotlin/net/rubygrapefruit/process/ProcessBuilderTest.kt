package net.rubygrapefruit.process

actual fun pwd(): List<String> {
    return listOf("powershell", "/c", "pwd")
}

actual fun ls(): List<String> {
    return listOf("powershell", "/c", "ls")
}