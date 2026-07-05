package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.DefaultJvmComponent
import net.rubygrapefruit.plugins.app.internal.componentRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmComponentPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            componentRegistry.applyToProject<DefaultJvmComponent<*>> { component ->
                component.mainSourceSet.kotlin.srcDirs(component.generatedSource)
            }
        }
    }
}