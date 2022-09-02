package net.rubygrapefruit.app

import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

interface JvmApplication : Application {
    override val distribution: JvmDistribution

    /**
     * The name of the module for this application.
     */
    val module: Property<String>

    val mainClass: Property<String>

    /**
     * The module path for this application. Includes the jar of the application plus those of all its runtime dependencies.
     */
    val outputModulePath: FileCollection

    val outputModuleNames: Provider<List<String>>
}
