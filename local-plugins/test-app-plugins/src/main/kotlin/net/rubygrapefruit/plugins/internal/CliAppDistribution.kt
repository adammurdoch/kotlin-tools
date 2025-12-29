package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

sealed interface AppDistribution {
    val distTask: String

    val distDir: Path
}

class CliAppDistribution(override val distTask: String, override val distDir: Path) : AppDistribution

class UiAppDistribution(override val distTask: String, override val distDir: Path) : AppDistribution
