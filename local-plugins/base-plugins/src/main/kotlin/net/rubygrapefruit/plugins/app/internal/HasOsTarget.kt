package net.rubygrapefruit.plugins.app.internal

/**
 * A component that targets a specific operating system.
 */
interface HasOsTarget {
    val target: OperatingSystem
}