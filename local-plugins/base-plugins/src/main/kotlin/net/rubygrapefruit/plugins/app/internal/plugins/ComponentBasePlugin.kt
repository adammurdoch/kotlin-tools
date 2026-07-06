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
                derive(component.common)
                component.visitTargets { target ->
                    derive(target)
                }
            }
            componentRegistry.deriveFrom<HasTests> { component ->
                derive(component.test)
            }
            componentRegistry.deriveFrom<HasDependencies> { component ->
                deriveFromSourceSet(component.sourceSetName) { sourceSet ->
                    sourceSet.dependencies {
                        component.dependencies.applyTo(this)
                    }
                }
            }
            componentRegistry.deriveFrom<HasGeneratedSource> { component ->
                deriveFromSourceSet(component.sourceSetName) { sourceSet ->
                    sourceSet.kotlin.srcDirs(component.generatedSource)
                }
            }
        }
    }
}