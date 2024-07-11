package net.rubygrapefruit.cli

internal interface HasPositionalUsage {
    fun usage(): PositionalUsage
}