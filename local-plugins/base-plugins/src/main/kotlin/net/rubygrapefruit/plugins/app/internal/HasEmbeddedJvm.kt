package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.provider.Property

interface HasEmbeddedJvm : MutableDistribution, HasTargetMachine, HasDistributionImage {
    val javaLauncherPath: Property<String>
}