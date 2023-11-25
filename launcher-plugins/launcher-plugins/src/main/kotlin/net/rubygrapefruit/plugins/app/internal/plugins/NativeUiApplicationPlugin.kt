package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.tasks.NativeLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class NativeUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(UiApplicationBasePlugin::class.java)
            multiplatformComponents.macOS {
                executable { }
            }
            applications.withApp<DefaultNativeUiApplication> { app ->
                if (HostMachine.current is MacOS) {
                    val nativeTarget = kotlin.targets.getByName(HostMachine.current.machine.kotlinTarget) as KotlinNativeTarget
                    val executable = nativeTarget.binaries.withType(Executable::class.java).first()
                    val binaryFile = layout.file(executable.linkTaskProvider.map { it.binary.outputFile })

                    val generatorTask = tasks.register("nativeLauncher", NativeLauncher::class.java) {
                        it.sourceDirectory.set(layout.buildDirectory.dir("generated-main"))
                        it.delegateClass.set(app.delegateClass)
                    }
                    withMacosMain(kotlin) {
                        it.kotlin.srcDir(generatorTask.flatMap { it.sourceDirectory })
                    }

                    app.distribution.launcherFile.set(binaryFile)
                }
            }

            val app = extensions.create("application", DefaultNativeUiApplication::class.java)
            applications.register(app)
        }
    }

    private fun withMacosMain(extension: KotlinMultiplatformExtension, action: (KotlinSourceSet) -> Unit) {
        val macosMain = extension.sourceSets.findByName("macosMain")
        if (macosMain != null) {
            action(macosMain)
        } else {
            extension.sourceSets.whenObjectAdded {
                if (it.name == "macosMain") {
                    action(it)
                }
            }
        }
    }
}