package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer

open class MultiPlatformComponentRegistry(private val project: Project) {
    private val unixSourceSets = mutableSetOf<String>()
    private val unixTestSourceSets = mutableSetOf<String>()
    private val machines = mutableSetOf<NativeMachine>()
    private val jvm = SimpleContainer<Boolean>()
    private var hasSourceSets = false
    val sourceSets = SourceSets(project)

    fun createSourceSets() {
        if (hasSourceSets) {
            return
        }
        hasSourceSets = true
        val hasJvm = jvm.all.isNotEmpty()
        val hasNative = machines.isNotEmpty()
        val desktop = hasJvm && hasNative
        if (unixSourceSets.isNotEmpty() || unixTestSourceSets.isNotEmpty() || desktop) {
            project.kotlin.applyDefaultHierarchyTemplate()
            createIntermediateSourceSet("unixMain", "nativeMain", unixSourceSets)
            createIntermediateSourceSet("unixTest", "nativeTest", unixTestSourceSets)
            if (desktop) {
                createIntermediateSourceSet("desktopMain", "commonMain", setOf("jvmMain", "nativeMain"))
                createIntermediateSourceSet("desktopTest", "commonTest", setOf("jvmTest", "nativeTest"))
            }
        }
    }

    fun nativeDesktop(config: KotlinNativeBinaryContainer.(NativeMachine) -> Unit = {}) {
        macOS(config)
        native(setOf(NativeMachine.LinuxX64, NativeMachine.WindowsX64), config)
    }

    fun macOS(config: KotlinNativeBinaryContainer.(NativeMachine) -> Unit = {}) {
        native(setOf(NativeMachine.MacOSArm64), config)
    }

    fun jvm() {
        jvm.add(true)
    }

    private fun createIntermediateSourceSet(name: String, parentName: String, childNames: Set<String>) {
        if (childNames.isEmpty()) {
            return
        }
        sourceSets.withSourceSet(parentName) { parent, container ->
            val intermediate = container.create(name) {
                it.dependsOn(parent)
            }
            for (childName in childNames) {
                sourceSets.withSourceSet(childName) { child, _ ->
                    child.dependsOn(intermediate)
                }
            }
        }
    }

    private fun native(targets: Set<NativeMachine>, config: KotlinNativeBinaryContainer.(NativeMachine) -> Unit) {
        for (target in targets) {
            if (machines.add(target)) {
                with(project.kotlin) {
                    when (target) {
                        NativeMachine.MacOSArm64 -> {
                            macosArm64 {
                                config(binaries, NativeMachine.MacOSArm64)
                            }
                            unixSourceSets.add("macosMain")
                            unixTestSourceSets.add("macosTest")
                        }

                        NativeMachine.LinuxX64 -> {
                            linuxX64 {
                                config(binaries, NativeMachine.LinuxX64)
                            }
                            unixSourceSets.add("linuxMain")
                            unixTestSourceSets.add("linuxTest")
                        }

                        NativeMachine.WindowsX64 -> {
                            mingwX64 {
                                config(binaries, NativeMachine.WindowsX64)
                            }
                        }
                    }
                }
            }
        }
    }
}