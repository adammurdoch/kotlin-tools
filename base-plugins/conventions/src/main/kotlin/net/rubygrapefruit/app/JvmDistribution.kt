package net.rubygrapefruit.app

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

interface JvmDistribution : Distribution {
    /**
     * The module JARs to copy into the distribution image.
     */
    val modulePath: ConfigurableFileCollection

    /**
     * The path to the `java` command embedded in the distribution, if any.
     */
    val javaLauncherPath: Property<String>
}
