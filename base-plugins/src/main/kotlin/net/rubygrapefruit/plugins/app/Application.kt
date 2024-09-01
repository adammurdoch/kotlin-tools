package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

interface Application {
    /**
     * The base name for the application.
     */
    val appName: Property<String>

    /**
     * All distributions of this application.
     */
    val distributions: Provider<List<Distribution>>

    /**
     * The debug distribution for this application to use on the current machine, if any.
     */
    val distribution: Provider<Distribution>
}