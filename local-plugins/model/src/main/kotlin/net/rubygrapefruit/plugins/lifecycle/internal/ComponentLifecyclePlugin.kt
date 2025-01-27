package net.rubygrapefruit.plugins.lifecycle.internal

import net.rubygrapefruit.plugins.lifecycle.ComponentDetails
import net.rubygrapefruit.plugins.lifecycle.Coordinates
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class ComponentLifecyclePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val model = extensions.create("component", ComponentDetails::class.java)
            model.releaseCoordinates.set(provider { Coordinates(group.toString(), name, version.toString()) })
        }
    }
}