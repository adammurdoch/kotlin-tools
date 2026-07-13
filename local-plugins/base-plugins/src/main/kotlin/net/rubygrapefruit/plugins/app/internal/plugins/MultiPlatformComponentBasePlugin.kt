package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
                        registerSibling(NativeTarget(machine, kotlin.targets.getByName(machine.kotlinTarget) as KotlinNativeTarget))
                    }
                }
            }
        }
    }
}