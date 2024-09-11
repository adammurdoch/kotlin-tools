package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

open class MultiPlatformComponentRegistry(private val project: Project) {
    private val unixSourceSets = mutableSetOf<String>()
    private val unixTestSourceSets = mutableSetOf<String>()
    private val machines = mutableSetOf<NativeMachine>()
    private val machineActions = mutableListOf<(NativeMachine, KotlinNativeTarget) -> Unit>()
    private val jvm = SimpleContainer<Boolean>()

    init {
        project.afterEvaluate {
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

    private fun createIntermediateSourceSet(name: String, parentName: String, children: Set<String>) {
        if (children.isEmpty()) {
            return
        }
        withSourceSet(parentName) { parent, sourceSets ->
            val intermediate = sourceSets.create(name) {
                it.dependsOn(parent)
            }
            for (childName in children) {
                withSourceSet(childName) { child, sourceSets ->
                    child.dependsOn(intermediate)
                }
            }
        }
    }

    private fun withSourceSet(name: String, action: (KotlinSourceSet, NamedDomainObjectContainer<KotlinSourceSet>) -> Unit) {
        val sourceSets = project.kotlin.sourceSets
        val sourceSet = sourceSets.findByName(name)
        if (sourceSet != null) {
            action(sourceSet, sourceSets)
        } else {
            sourceSets.whenObjectAdded { sourceSet ->
                if (sourceSet.name == name) {
                    action(sourceSet, sourceSets)
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