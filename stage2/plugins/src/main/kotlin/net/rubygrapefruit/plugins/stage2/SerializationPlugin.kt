package net.rubygrapefruit.plugins.stage2

import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class SerializationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(BuildConstants.constants.serialization.plugin.id)

            dependencies.add("implementation", BuildConstants.constants.serialization.library.json.coordinates)
        }
    }
}