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
     * A development distribution for this application to use on the current machine, if any.
     *
     * Has no value when no distribution of the application can be built on the current machine.
     */
    val distribution: Provider<Distribution>

    /**
     * An installation of the development distribution for this application on the current machine, if any.
     *
     * Has no value when the application cannot be installed on the current machine, for example, when it cannot be built for or run on the current machine.
     */
    val localInstallation: Provider<Installation>

    /**
     * All installations of this application.
     */
    val installations: Provider<List<Installation>>
}