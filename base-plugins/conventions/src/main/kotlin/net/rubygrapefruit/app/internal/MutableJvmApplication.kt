package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmApplication
import org.gradle.api.file.ConfigurableFileCollection

/**
 * A JVM based application.
 */
interface MutableJvmApplication : JvmApplication {
    override val distribution: DefaultJvmDistribution

    /**
     * The runtime module path for this application. Includes the jar of the application plus those of all its runtime dependencies.
     */
    val runtimeModulePath: ConfigurableFileCollection

    /**
     * The packaging of this application.
     */
    var packaging: JvmApplicationPackaging
}