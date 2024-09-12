package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.provider.Property

interface HasEmbeddedJvm : MutableDistribution {
    val targetMachine: NativeMachine

    val javaLauncherPath: Property<String>
}