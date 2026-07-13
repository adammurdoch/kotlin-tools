package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComponentBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            componentRegistry.each<MutableComponent> {
                prepare { _ ->
                    multiplatformComponents.createSourceSets()
                }
            }
            componentRegistry.each<PlatformContribution> {
                derive { component ->
                    register(component.main)
                }
            }
            componentRegistry.each<HasTests> {
                derive { component ->
                    register(component.test)
                }
            }
            componentRegistry.each<HasDependencies> {
                derive { component ->
                    deriveFromSourceSet(component.sourceSetName) { sourceSet ->
                        sourceSet.dependencies {
                            component.dependencies.applyTo(this)
                        }
                    }
                }
            }
            componentRegistry.each<HasGeneratedSource> {
                derive { component ->
                    deriveFromSourceSet(component.sourceSetName) { sourceSet ->
                        sourceSet.kotlin.srcDirs(component.generatedSource)
                    }
                }
            }
        }
    }
}