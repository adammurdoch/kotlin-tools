package net.rubygrapefruit.app

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

interface JvmDistribution : Distribution {
    /**
     * The module JARs to copy into the distribution image.
     */
    val modulePath: ConfigurableFileCollection

    val modulePathNames: ListProperty<String>

    /**
     * The path to the `java` command embedded in the distribution, if any.
     */
    val javaLauncherPath: Property<String>
}
