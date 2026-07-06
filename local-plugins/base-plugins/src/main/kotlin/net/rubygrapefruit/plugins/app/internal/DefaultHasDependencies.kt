package net.rubygrapefruit.plugins.app.internal

class DefaultHasDependencies(override val sourceSetName: String): HasDependencies {
    override val dependencies = DefaultDependencies()
}