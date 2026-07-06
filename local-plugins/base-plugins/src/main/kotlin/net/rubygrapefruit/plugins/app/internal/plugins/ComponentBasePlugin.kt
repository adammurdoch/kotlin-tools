package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComponentBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            componentRegistry.applyToProject<MutableComponent> { _ ->
                multiplatformComponents.createSourceSets()
            }
            componentRegistry.deriveFrom<HasTargets> { component ->
                kotlin.sourceSets.getByName("commonMain").dependencies {
                    component.common.applyTo(this)
                }
                kotlin.sourceSets.getByName("commonTest").dependencies {
                    component.test.applyTo(this)
                }
                component.visitTargets { target ->
                    derive(target)
                }
            }
            componentRegistry.applyToProject<HasGeneratedSource> { component ->
                component.sourceSet.kotlin.srcDirs(component.generatedSource)
            }
        }
    }
}