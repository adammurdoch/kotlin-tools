package net.rubygrapefruit.plugins.stage2

sealed class Sample {
}

class JvmCliApp(val name: String): Sample()
