package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComponentBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            componentRegistry.from<MutableComponent> {
                derive { _ ->
                    multiplatformComponents.createSourceSets()
                }
            }
            componentRegistry.from<HasTargets> {
                derive { component ->
                    register(component.common)
                    component.visitTargets { target ->
                        register(target)
                    }
                }
            }
            componentRegistry.from<HasTests> {
                derive { component ->
                    register(component.test)
                }
            }
            componentRegistry.from<HasDependencies> {
                derive { component ->
                    deriveFromSourceSet(component.sourceSetName) { sourceSet ->
                        sourceSet.dependencies {
                            component.dependencies.applyTo(this)
                        }
                    }
                }
            }
            componentRegistry.from<HasGeneratedSource> {
                derive { component ->
                    deriveFromSourceSet(component.sourceSetName) { sourceSet ->
                        sourceSet.kotlin.srcDirs(component.generatedSource)
                    }
                }
            }
        }
    }
}