package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Property

interface JvmApplication : Application, JvmComponent<Dependencies> {
    /**
     * The name of the main class for this application.
     */
    val mainClass: Property<String>
}
