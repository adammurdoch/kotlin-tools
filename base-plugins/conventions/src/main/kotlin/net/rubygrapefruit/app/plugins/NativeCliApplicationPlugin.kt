package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.io.ByteArrayOutputStream

open class NativeCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<DefaultNativeCliApplication> { app ->
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
                    linuxX64 {
                        binaries {
                            executable {
                            }
                        }
                    }
                    mingwX64 {
                        binaries {
                            executable {
                            }
                        }
                    }
                    val commonMain = sourceSets.getByName("commonMain")
                    val unixMain = sourceSets.create("unixMain") {
                        it.dependsOn(commonMain)
                    }
                    val macOsMain = sourceSets.create("macosMain") {
                        it.dependsOn(unixMain)
                    }
                    sourceSets.getByName("macosX64Main") {
                        it.dependsOn(macOsMain)
                    }
                    sourceSets.getByName("macosArm64Main") {
                        it.dependsOn(macOsMain)
                    }
                    sourceSets.getByName("linuxX64Main") {
                        it.dependsOn(unixMain)
                    }
                    val unixTest = sourceSets.create("unixTest") {
                        it.dependsOn(unixMain)
                        it.dependsOn(sourceSets.getByName("commonTest"))
                    }
                    val macOsTest = sourceSets.create("macosTest") {
                        it.dependsOn(unixTest)
                    }
                    sourceSets.getByName("macosX64Test") {
                        it.dependsOn(macOsTest)
                    }
                    sourceSets.getByName("macosArm64Test") {
                        it.dependsOn(macOsTest)
                    }
                    sourceSets.getByName("linuxX64Test") {
                        it.dependsOn(unixTest)
                    }
                }

                val extension = extensions.getByType(KotlinMultiplatformExtension::class.java)
                val nativeTarget = extension.targets.getByName(nativeTargetName) as KotlinNativeTarget
                val executable = nativeTarget.binaries.withType(Executable::class.java).first()
                val binaryFile = layout.file(executable.linkTaskProvider.map { it.binary.outputFile })
                app.outputBinary.set(binaryFile)
                app.distribution.launcherFilePath.set(app.appName.map { if (currentOs() == Windows) "$it.exe" else it })
                app.distribution.launcherFile.set(binaryFile)
            }

            val app = extensions.create("application", DefaultNativeCliApplication::class.java)
            applications.register(app, app.distribution)
        }
    }
}


private val nativeTargetName by lazy {
    val os = currentOs()
    if (os == Linux) {
        "linuxX64"
    } else if (os == Windows) {
        "mingwX64"
    } else {
        val output = ByteArrayOutputStream()
        val builder = ProcessBuilder("sysctl", "-n", "machdep.cpu.brand_string")
        val process = builder.start()
        process.inputStream.copyTo(output)
        process.errorStream.copyTo(System.err)
        if (output.toString().contains("Apple M1")) {
            "macosArm64"
        } else {
            "macosX64"
        }
    }
}