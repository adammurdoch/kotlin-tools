package net.rubygrapefruit.plugins.stage2

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class NoOpReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            extensions.create("component", ComponentDetails::class.java)
        }
    }
}