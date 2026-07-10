package net.rubygrapefruit.plugins.app.internal

/**
 * Represents the source, tests, dependencies and other inputs for a particular "platform", either a specific target (e.g. macOS arm64) or a "virtual" target (e.g. macOS or common).
 */
interface PlatformContribution {
    val main: HasDependencies
}