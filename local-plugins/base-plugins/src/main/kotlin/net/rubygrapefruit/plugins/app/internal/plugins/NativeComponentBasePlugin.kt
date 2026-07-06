package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.DefaultNativeComponent
import net.rubygrapefruit.plugins.app.internal.RealizedNativeComponent
import net.rubygrapefruit.plugins.app.internal.componentRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project

class NativeComponentBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            componentRegistry.deriveFrom<DefaultNativeComponent> { component ->
                deriveFromSourceSet(component.mainSourceSetName) { sourceSet ->
                    sourceSet.dependencies {
                        component.dependencies.applyTo(this)
                    }
                    derive(RealizedNativeComponent(sourceSet, component.generatedSource))
                }
            }
        }
    }
}