package net.rubygrapefruit.plugins.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BaseJvmLibraryPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
        }
    }
}