package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class MultiPlatformComponentBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            target.extensions.create("multiplatformComponents", MultiPlatformComponentRegistry::class.java)

            componentRegistry.each<MutableComponent> {
                prepare { _ ->
                    multiplatformComponents.createSourceSets()
                }
            }

            componentRegistry.each<HasTargets> {
                derive { component ->
                    component.visitPlatforms { contribution ->
                        register(contribution)
                    }
                }
            }

            componentRegistry.each<HasOsTarget> {
                initialize { component ->
                    component.visitMachines { machine ->
                        // Declare the Kotlin targets eagerly at configuration time, so that these are in the appropriate state to allow
                        // executables to be declared on the target later
                        @Suppress("DEPRECATION")
                        when (machine) {
                            MacOsArm64 -> kotlin.macosArm64()
                            MacOsX64 -> kotlin.macosX64()
                            LinuxX64 -> kotlin.linuxX64()
                            LinuxArm64 -> kotlin.linuxArm64()
                            WindowsX64 -> kotlin.mingwX64()
                        }
                    }
                    component.createTargets()
                }

                derive { component ->
                    for (machine in component.target.machines) {
                        val kotlinTarget = kotlin.targets.getByName(machine.kotlinTarget) as KotlinNativeTarget
                        val canBuild = NativeMachine.current.canBuild(machine)
                        registerSibling(RealizedNativeTarget(canBuild, machine, kotlinTarget))
                    }
                }
            }
        }
    }
}