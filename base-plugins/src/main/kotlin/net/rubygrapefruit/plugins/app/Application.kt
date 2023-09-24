package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Property

interface Application {
    /**
     * The base name for the application.
     */
    val appName: Property<String>

    /**
     * The debug distribution for this application.
     */
    val distribution: net.rubygrapefruit.plugins.app.Distribution
}