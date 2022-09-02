package net.rubygrapefruit.app

import org.gradle.api.provider.Property

interface Application {
    val appName: Property<String>

    /**
     * The debug distribution for this application.
     */
    val distribution: Distribution
}