package net.rubygrapefruit.plugins.internal

sealed interface AppDistribution {
    val distTask: String
}

class CliAppDistribution(override val distTask: String) : AppDistribution

class UiAppDistribution(override val distTask: String) : AppDistribution
