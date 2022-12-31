package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.NativeMachine
import net.rubygrapefruit.app.internal.ComponentTargets
import net.rubygrapefruit.app.internal.DefaultNativeUiApplication
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.internal.multiplatformComponents
import net.rubygrapefruit.app.tasks.NativeLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class NativeUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(UiApplicationBasePlugin::class.java)
            multiplatformComponents.registerSourceSets(ComponentTargets(false, setOf(NativeMachine.MacOSArm64, NativeMachine.MacOSX64)))
            applications.withApp<DefaultNativeUiApplication> { app ->
                with(extensions.getByType(KotlinMultiplatformExtension::class.java)) {
                    macosX64 {
                        binaries {
                            executable {
                            }
                        }
                    }
                    macosArm64 {
                        binaries {
                            executable {
                            }
                        }
                    }
                }

                val extension = extensions.getByType(KotlinMultiplatformExtension::class.java)
                val nativeTarget = extension.targets.getByName(NativeMachine.MacOSArm64.kotlinTarget) as KotlinNativeTarget
                val executable = nativeTarget.binaries.withType(Executable::class.java).first()
                val binaryFile = layout.file(executable.linkTaskProvider.map { it.binary.outputFile })

                val generatorTask = tasks.register("nativeLauncher", NativeLauncher::class.java) {
                    it.sourceDirectory.set(layout.buildDirectory.dir("generated-main"))
                    it.delegateClass.set(app.delegateClass)
                }
                val macosMain = extension.sourceSets.getByName("macosMain")
                macosMain.kotlin.srcDir(generatorTask.flatMap { it.sourceDirectory })

                app.distribution.launcherFile.set(binaryFile)
            }

            val app = extensions.create("application", DefaultNativeUiApplication::class.java)
            applications.register(app)
        }
    }
}