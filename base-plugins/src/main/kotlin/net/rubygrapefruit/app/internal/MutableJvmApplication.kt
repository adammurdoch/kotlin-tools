package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmApplication
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

/**
 * A JVM based application.
 */
interface MutableJvmApplication : JvmApplication, MutableApplication {
    /**
     * The runtime module path for this application. Includes the jar of the application plus those of all its runtime dependencies.
     */
    val runtimeModulePath: ConfigurableFileCollection

    /**
     * The path to the `java` command embedded in the distribution, if any.
     */
    val javaLauncherPath: Property<String>

    /**
     * The packaging of this application.
     */
    var packaging: JvmApplicationPackaging
}