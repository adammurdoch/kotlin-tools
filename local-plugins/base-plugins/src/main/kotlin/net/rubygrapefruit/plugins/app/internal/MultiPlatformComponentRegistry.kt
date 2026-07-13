package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.Project

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

    fun nativeDesktop() {
        macOS()
        native(setOf(NativeMachine.LinuxX64, NativeMachine.WindowsX64))
    }

    fun macOS() {
        native(setOf(NativeMachine.MacOSArm64))
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

    private fun native(targets: Set<NativeMachine>) {
        for (target in targets) {
            if (machines.add(target)) {
                when (target) {
                    NativeMachine.MacOSArm64 -> {
                        unixSourceSets.add("macosMain")
                        unixTestSourceSets.add("macosTest")
                    }

                    NativeMachine.LinuxX64 -> {
                        unixSourceSets.add("linuxMain")
                        unixTestSourceSets.add("linuxTest")
                    }

                    NativeMachine.WindowsX64 -> {
                    }
                }
            }
        }
    }
}