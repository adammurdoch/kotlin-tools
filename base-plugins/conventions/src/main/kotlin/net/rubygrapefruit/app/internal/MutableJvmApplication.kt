package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmApplication
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty

/**
 * A JVM based application.
 */
interface MutableJvmApplication : JvmApplication {
    /**
     * The module path for this application. Includes the jar of the application plus those of all its runtime dependencies.
     */
    val outputModulePath: ConfigurableFileCollection

    /**
     * The basename of the files in the module path for this application.
     */
    val outputModuleNames: ListProperty<String>

    var packaging: JvmApplicationPackaging
}