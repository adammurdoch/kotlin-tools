package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

open class MultiPlatformComponentRegistry(private val project: Project) {
    private val desktopSourceSets = mutableSetOf<String>()
    private val unixSourceSets = mutableSetOf<String>()
    private val unixTestSourceSets = mutableSetOf<String>()
    private val machines = mutableSetOf<NativeMachine>()
    private val machineActions = mutableListOf<(NativeMachine, KotlinNativeTarget) -> Unit>()
    private val jvm = SimpleContainer<Boolean>()

    init {
        project.afterEvaluate {
            createIntermediateSourceSet("unixMain", "nativeMain", unixSourceSets)
            createIntermediateSourceSet("unixTest", "nativeTest", unixTestSourceSets)
            createIntermediateSourceSet("desktopMain", "commonMain", desktopSourceSets)
        }
    }

    fun nativeDesktop(config: KotlinNativeBinaryContainer.(NativeMachine) -> Unit = {}) {
        macOS(config)
        native(setOf(NativeMachine.LinuxX64, NativeMachine.WindowsX64), config)
    }

    fun macOS(config: KotlinNativeBinaryContainer.(NativeMachine) -> Unit = {}) {
        native(setOf(NativeMachine.MacOSX64, NativeMachine.MacOSArm64), config)
    }

    fun jvm(targetVersion: Provider<Int>) {
        with(project.kotlin) {
            jvmToolchain {
                it.languageVersion.set(targetVersion.map { JavaLanguageVersion.of(it) })
            }
            jvm()
        }
        jvm.add(true)
        desktopSourceSets.add("jvmMain")
    }

    fun browser() {
        with(project.kotlin) {
            js {
                browser()
            }
        }
    }

    fun eachNativeTarget(action: (NativeMachine, KotlinNativeTarget) -> Unit) {
        machineActions.add(action)
        for (machine in machines) {
            action(machine, project.kotlin.targets.getByName(machine.kotlinTarget) as KotlinNativeTarget)
        }
    }

    fun jvmTarget(action: () -> Unit) {
        jvm.each { action() }
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

    private fun native(targets: Set<NativeMachine>, config: KotlinNativeBinaryContainer.(NativeMachine) -> Unit) {
        with(project.kotlin) {
            if (targets.contains(NativeMachine.MacOSX64)) {
                macosX64 {
                    config(binaries, NativeMachine.MacOSX64)
                }
                desktopSourceSets.add("unixMain")
                unixSourceSets.add("macosMain")
                unixTestSourceSets.add("macosTest")
            }
            if (targets.contains(NativeMachine.MacOSArm64)) {
                macosArm64 {
                    config(binaries, NativeMachine.MacOSArm64)
                }
                desktopSourceSets.add("unixMain")
                unixSourceSets.add("macosMain")
                unixTestSourceSets.add("macosTest")
            }
            if (targets.contains(NativeMachine.LinuxX64)) {
                linuxX64 {
                    config(binaries, NativeMachine.LinuxX64)
                }
                desktopSourceSets.add("unixMain")
                unixSourceSets.add("linuxMain")
                unixTestSourceSets.add("linuxTest")
            }
            if (targets.contains(NativeMachine.WindowsX64)) {
                mingwX64 {
                    config(binaries, NativeMachine.WindowsX64)
                }
                desktopSourceSets.add("mingwX64Main")
            }
        }
        for (target in targets) {
            if (machines.add(target)) {
                val nativeTarget = project.kotlin.targets.getByName(target.kotlinTarget) as KotlinNativeTarget
                for (action in machineActions) {
                    action(target, nativeTarget)
                }
            }
        }
    }
}