package net.rubygrapefruit.plugins.app.internal

/**
 * A component with more than one target.
 */
interface HasTargets {
    fun visitPlatforms(visitor: (PlatformContribution) -> Unit)
}