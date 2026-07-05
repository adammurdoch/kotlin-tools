package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.HasGeneratedSource
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import net.rubygrapefruit.plugins.app.internal.componentRegistry
import net.rubygrapefruit.plugins.app.internal.multiplatformComponents
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComponentBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            componentRegistry.applyToProject<MutableComponent> { _ ->
                multiplatformComponents.createSourceSets()
            }
            componentRegistry.applyToProject<HasGeneratedSource> { component ->
                component.sourceSet.kotlin.srcDirs(component.generatedSource)
            }
        }
    }
}