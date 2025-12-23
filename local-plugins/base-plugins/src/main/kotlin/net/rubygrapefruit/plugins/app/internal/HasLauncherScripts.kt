package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.provider.Property

interface HasLauncherScripts : MutableDistribution, HasDistributionImage {
    /**
     * The path to the `java` command embedded in the distribution, if any. If no value, use the `java` command from the PATH.
     */
    val javaLauncherPath: Property<String>
}