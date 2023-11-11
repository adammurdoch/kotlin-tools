package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.ComponentTargets
import net.rubygrapefruit.plugins.app.internal.multiplatformComponents
import org.gradle.api.Plugin
import org.gradle.api.Project

class NativeDesktopLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(LibraryBasePlugin::class.java)

            multiplatformComponents.registerSourceSets(ComponentTargets(null, setOf(NativeMachine.LinuxX64, NativeMachine.MacOSX64, NativeMachine.MacOSArm64, NativeMachine.WindowsX64)))
        }
    }
}