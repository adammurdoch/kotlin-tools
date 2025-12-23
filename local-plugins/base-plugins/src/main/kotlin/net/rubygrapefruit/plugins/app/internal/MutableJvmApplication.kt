package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmApplication
import org.gradle.api.file.ConfigurableFileCollection

/**
 * A JVM based application.
 */
interface MutableJvmApplication : JvmApplication, MutableApplication {
    /**
     * The runtime module path for this application. Includes the jar of the application plus those of all its runtime dependencies.
     */
    val runtimeModulePath: ConfigurableFileCollection
}