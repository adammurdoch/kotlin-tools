package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.Versions
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BuildConstantsPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            extensions.add("versions", Versions)
        }
    }
}