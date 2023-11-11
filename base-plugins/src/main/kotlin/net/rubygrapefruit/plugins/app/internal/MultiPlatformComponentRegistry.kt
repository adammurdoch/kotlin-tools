package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

open class MultiPlatformComponentRegistry(private val project: Project) {
    private val kotlin: KotlinMultiplatformExtension
        get() = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

    fun native(targets: Set<NativeMachine>) {
        if (targets.isEmpty()) {
            return
        }
        with(kotlin) {
            val unixSourceSets = mutableSetOf<String>()
            val unixTestSourceSets = mutableSetOf<String>()

            if (targets.contains(NativeMachine.MacOSX64)) {
                macosX64()
                unixSourceSets.add("macosMain")
                unixTestSourceSets.add("macosTest")
            }
            if (targets.contains(NativeMachine.MacOSArm64)) {
                macosArm64()
                unixSourceSets.add("macosMain")
                unixTestSourceSets.add("macosTest")
            }
            if (targets.contains(NativeMachine.LinuxX64)) {
                linuxX64()
                unixSourceSets.add("linuxMain")
                unixTestSourceSets.add("linuxTest")
            }
            if (targets.contains(NativeMachine.WindowsX64)) {
                mingwX64()
            }
            applyDefaultHierarchyTemplate()

            val nativeMain = sourceSets.getByName("nativeMain")
            val nativeTest = sourceSets.getByName("nativeTest")

            if (unixSourceSets.isNotEmpty()) {
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
            }
        }
    }

    fun jvm(targetVersion: Provider<Int>) {
        with(kotlin) {
            jvmToolchain {
                it.languageVersion.set(targetVersion.map { JavaLanguageVersion.of(it) })
            }
            jvm()
        }
    }

    fun browser() {
        with(kotlin) {
            js {
                browser()
            }
        }
    }
}