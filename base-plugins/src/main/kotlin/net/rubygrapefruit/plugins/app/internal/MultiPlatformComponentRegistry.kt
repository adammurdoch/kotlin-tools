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
    private val machines = mutableSetOf<NativeMachine>()
    val targetMachines: Set<NativeMachine>
        get() = machines

    init {
        project.afterEvaluate {
            createIntermediateSourceSet("unixMain", "nativeMain", unixSourceSets)
            createIntermediateSourceSet("unixTest", "nativeTest", unixTestSourceSets)
        }
    }

    private fun createIntermediateSourceSet(name: String, parent: String, children: MutableSet<String>) {
        if (children.isEmpty()) {
            return
        }
        with(project.kotlin) {
            var intermediate: KotlinSourceSet? = null
            sourceSets.whenObjectAdded { sourceSet ->
                if (sourceSet.name == parent) {
                    intermediate = sourceSets.create(name) {
                        it.dependsOn(sourceSet)
                    }
                    val iter = children.iterator()
                    for (childName in iter) {
                        val child = sourceSets.findByName(childName)
                        if (child != null) {
                            child.dependsOn(intermediate!!)
                            iter.remove()
                        }
                    }
                } else if (intermediate != null && children.contains(sourceSet.name)) {
                    children.remove(sourceSet.name)
                    sourceSet.dependsOn(intermediate!!)
                }
            }
        }
    }

    fun desktop(config: KotlinNativeBinaryContainer.(NativeMachine) -> Unit = {}) {
        macOS(config)
        native(setOf(NativeMachine.LinuxX64, NativeMachine.WindowsX64), config)
    }

    fun macOS(config: KotlinNativeBinaryContainer.(NativeMachine) -> Unit = {}) {
        native(setOf(NativeMachine.MacOSX64, NativeMachine.MacOSArm64), config)
    }

    private fun native(targets: Set<NativeMachine>, config: KotlinNativeBinaryContainer.(NativeMachine) -> Unit) {
        machines.addAll(targets)
        with(project.kotlin) {
            if (targets.contains(NativeMachine.MacOSX64)) {
                macosX64 {
                    config(binaries, NativeMachine.MacOSX64)
                }
                unixSourceSets.add("macosMain")
                unixTestSourceSets.add("macosTest")
            }
            if (targets.contains(NativeMachine.MacOSArm64)) {
                macosArm64 {
                    config(binaries, NativeMachine.MacOSArm64)
                }
                unixSourceSets.add("macosMain")
                unixTestSourceSets.add("macosTest")
            }
            if (targets.contains(NativeMachine.LinuxX64)) {
                linuxX64 {
                    config(binaries, NativeMachine.LinuxX64)
                }
                unixSourceSets.add("linuxMain")
                unixTestSourceSets.add("linuxTest")
            }
            if (targets.contains(NativeMachine.WindowsX64)) {
                mingwX64 {
                    config(binaries, NativeMachine.WindowsX64)
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