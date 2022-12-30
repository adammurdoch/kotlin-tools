package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.JvmApplication
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty

/**
 * A JVM based application.
 */
interface MutableJvmApplication : JvmApplication {
    override val outputModulePath: ConfigurableFileCollection

    override val outputModuleNames: ListProperty<String>
}