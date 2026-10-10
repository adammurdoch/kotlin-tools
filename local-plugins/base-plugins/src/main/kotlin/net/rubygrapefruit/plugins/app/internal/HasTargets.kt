package net.rubygrapefruit.plugins.app.internal

/**
 * A component with more than one target.
 */
interface HasTargets {
    /**
     * Visits platforms that have not been created using [net.rubygrapefruit.plugins.app.internal.component.ComponentFactory].
     */
    fun visitOtherPlatforms(visitor: (PlatformContribution) -> Unit)
}