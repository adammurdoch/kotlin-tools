package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

interface JvmApplication : Application, JvmComponent<Dependencies> {
    /**
     * The module for this application.
     */
    @get:Nested
    val module: JvmModule

    /**
     * The name of the main class for this application.
     */
    val mainClass: Property<String>
}
