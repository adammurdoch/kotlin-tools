package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

open class MultiPlatformComponentRegistry(private val project: Project) {
    private val unixSourceSets = mutableSetOf<String>()
    private val unixTestSourceSets = mutableSetOf<String>()

    init {
        project.afterEvaluate {
            if (unixSourceSets.isNotEmpty()) {
                with(it.kotlin) {
                    println("-> DEFINE UNIX SOURCE SETS")
                    var unixMain: KotlinSourceSet? = null
                    var unixTest: KotlinSourceSet? = null
                    sourceSets.whenObjectAdded { sourceSet ->
                        if (sourceSet.name == "nativeMain") {
                            unixMain = sourceSets.create("unixMain") {
                                println("-> ${it.name} -> ${sourceSet.name}")
                                it.dependsOn(sourceSet)
                            }
                            val iter = unixSourceSets.iterator()
                            for (name in iter) {
                                val s = sourceSets.findByName(name)
                                if (s != null) {
                                    println("-> ${s.name} -> ${unixMain!!.name}")
                                    s.dependsOn(unixMain!!)
                                    iter.remove()
                                }
                            }
                        } else if (unixMain != null && unixSourceSets.contains(sourceSet.name)) {
                            println("-> ${sourceSet.name} -> ${unixMain!!.name}")
                            unixSourceSets.remove(sourceSet.name)
                            sourceSet.dependsOn(unixMain!!)
                        } else if (sourceSet.name == "nativeTest") {
                            unixTest = sourceSets.create("unixTest") {
                                println("-> ${it.name} -> ${sourceSet.name}")
                                it.dependsOn(sourceSet)
                            }
                        } else if (unixTest != null && unixTestSourceSets.contains(sourceSet.name)) {
                            println("-> ${sourceSet.name} -> ${unixTest!!.name}")
                            unixTestSourceSets.remove(sourceSet.name)
                            sourceSet.dependsOn(unixTest!!)
                        }
                    }

                    /*
                    applyDefaultHierarchyTemplate()
                    println("-> SOURCE SETS: ${sourceSets.names}")
                    val nativeMain = sourceSets.getByName("nativeMain")
                    val nativeTest = sourceSets.getByName("nativeTest")
                    val unixMain = sourceSets.create("unixMain") {
                        it.dependsOn(nativeMain)
                    }
                    for (sourceSet in unixSourceSets) {
                        sourceSets.getByName(sourceSet).dependsOn(unixMain)
                    }
                    val unixTest = sourceSets.create("unixTest") {
                        it.dependsOn(nativeTest)
                    }
                    for (sourceSet in unixTestSourceSets) {
                        sourceSets.getByName(sourceSet).dependsOn(unixTest)
                    }
                    */
                }
            }
        }
    }

    fun desktop(config: KotlinNativeBinaryContainer.() -> Unit = {}) {
        macOS(config)
        native(setOf(NativeMachine.LinuxX64, NativeMachine.WindowsX64), config)
    }

    fun macOS(config: KotlinNativeBinaryContainer.() -> Unit = {}) {
        native(setOf(NativeMachine.MacOSX64, NativeMachine.MacOSArm64), config)
    }

    private fun native(targets: Set<NativeMachine>, config: KotlinNativeBinaryContainer.() -> Unit) {
        with(project.kotlin) {
            if (targets.contains(NativeMachine.MacOSX64)) {
                macosX64 {
                    config(binaries)
                }
                unixSourceSets.add("macosMain")
                unixTestSourceSets.add("macosTest")
            }
            if (targets.contains(NativeMachine.MacOSArm64)) {
                macosArm64 {
                    config(binaries)
                }
                unixSourceSets.add("macosMain")
                unixTestSourceSets.add("macosTest")
            }
            if (targets.contains(NativeMachine.LinuxX64)) {
                linuxX64 {
                    config(binaries)
                }
                unixSourceSets.add("linuxMain")
                unixTestSourceSets.add("linuxTest")
            }
            if (targets.contains(NativeMachine.WindowsX64)) {
                mingwX64 {
                    config(binaries)
                }
            }
        }
    }

    fun jvm(targetVersion: Provider<Int>) {
        with(project.kotlin) {
            jvmToolchain {
                it.languageVersion.set(targetVersion.map { JavaLanguageVersion.of(it) })
            }
            jvm()
        }
    }

    fun browser() {
        with(project.kotlin) {
            js {
                browser()
            }
        }
    }
}