package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.NativeMachine
import net.rubygrapefruit.app.internal.ComponentTargets
import net.rubygrapefruit.app.internal.multiplatformComponents
import org.gradle.api.Plugin
import org.gradle.api.Project

class NativeLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(LibraryBasePlugin::class.java)

            multiplatformComponents.registerSourceSets(ComponentTargets(null, setOf(NativeMachine.LinuxX64, NativeMachine.MacOSX64, NativeMachine.MacOSArm64, NativeMachine.WindowsX64)))
        }
    }
}