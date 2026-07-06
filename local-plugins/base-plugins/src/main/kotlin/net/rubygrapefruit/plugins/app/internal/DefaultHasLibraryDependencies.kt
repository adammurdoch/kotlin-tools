package net.rubygrapefruit.plugins.app.internal

class DefaultHasLibraryDependencies(override val sourceSetName: String): HasDependencies {
    override val dependencies = DefaultLibraryDependencies()
}