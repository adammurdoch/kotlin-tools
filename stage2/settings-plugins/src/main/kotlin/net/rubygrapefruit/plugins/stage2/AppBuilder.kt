package net.rubygrapefruit.plugins.stage2

sealed class AppBuilder {
    fun cliArgs(vararg args: String) {
    }

    fun expectedOutput(text: String) {
    }
}

class JvmCliAppBuilder : AppBuilder() {
}

class NativeCliAppBuilder : AppBuilder() {
}