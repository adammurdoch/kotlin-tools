package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.NativeMachine
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
                    // Declare the Kotlin targets eagerly at configuration time, so that these are in the appropriate state to allow
                    // executables to be declared on the target later
                    for (machine in component.target.machines) {
                        when (machine) {
                            NativeMachine.MacOSArm64 -> kotlin.macosArm64()
                            NativeMachine.LinuxX64 -> kotlin.linuxX64()
                            NativeMachine.WindowsX64 -> kotlin.mingwX64()
                        }
                    }
                }
                derive { component ->
                    for (machine in component.target.machines) {
                        val kotlinTarget = kotlin.targets.getByName(machine.kotlinTarget) as KotlinNativeTarget
                        val canBuild = HostMachine.current.canBuild(machine)
                        registerSibling(RealizedNativeTarget(canBuild, machine, kotlinTarget))
                    }
                }
            }
        }
    }
}