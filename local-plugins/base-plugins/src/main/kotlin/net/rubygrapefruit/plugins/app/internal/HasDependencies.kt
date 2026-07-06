package net.rubygrapefruit.plugins.app.internal

interface HasDependencies {
    val sourceSetName: String

    val dependencies: DefaultDependencies
}