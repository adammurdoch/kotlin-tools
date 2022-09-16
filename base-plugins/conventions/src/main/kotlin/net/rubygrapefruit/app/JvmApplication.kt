package net.rubygrapefruit.app

import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Nested

interface JvmApplication : Application {
    override val distribution: JvmDistribution

    /**
     * The module for this application.
     */
    @get:Nested
    val module: JvmModule

    /**
     * The name of the main class for this application.
     */
    val mainClass: Property<String>

    /**
     * The module path for this application. Includes the jar of the application plus those of all its runtime dependencies.
     */
    val outputModulePath: FileCollection

    val outputModuleNames: Provider<List<String>>
}
