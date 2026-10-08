package net.rubygrapefruit.plugins.app.internal

/**
 * A set of dependencies to be associated with a Kotlin source set.
 */
interface HasDependencies {
    val sourceSetName: String

    val dependencies: DefaultDependencies
}