package net.rubygrapefruit.plugins.lifecycle.internal

import net.rubygrapefruit.plugins.lifecycle.ComponentDetails
import net.rubygrapefruit.plugins.lifecycle.Coordinates
import net.rubygrapefruit.plugins.lifecycle.VersionNumber
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class ComponentLifecyclePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val model = extensions.create("component", ComponentDetails::class.java)
            model.nextVersion.convention("0.0.1-milestone-1")
            model.targetVersion.convention(model.nextVersion.map { v -> VersionNumber.of(v).milestone() })
            model.targetCoordinates.set(model.targetVersion.map { v -> Coordinates(group.toString(), name, v.version) })
            model.releaseCoordinates.set(model.targetVersion.map { v -> Coordinates(group.toString(), name, v.released().version) })

            version = ProjectVersion(model.targetVersion)
        }
    }
}