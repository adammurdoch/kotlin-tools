package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.HasOsTarget
import net.rubygrapefruit.plugins.app.internal.HasTargets
import net.rubygrapefruit.plugins.app.internal.NativeTarget
import net.rubygrapefruit.plugins.app.internal.componentRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project

class MultiPlatformComponentBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            componentRegistry.each<HasTargets> {
                derive { component ->
                    component.visitPlatforms { contribution ->
                        register(contribution)
                    }
                }
            }
            componentRegistry.each<HasOsTarget> {
                derive { component ->
                    for (machine in component.target.machines) {
                        register(NativeTarget(machine))
                    }
                }
            }
            componentRegistry.each<NativeTarget> {
                derive { _ ->
                }
            }
        }
    }
}