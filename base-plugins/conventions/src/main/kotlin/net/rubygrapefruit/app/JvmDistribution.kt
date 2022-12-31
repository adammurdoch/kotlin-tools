package net.rubygrapefruit.app

import org.gradle.api.provider.Property

interface JvmDistribution : Distribution {
    /**
     * The path to the `java` command embedded in the distribution, if any.
     */
    val javaLauncherPath: Property<String>
}
