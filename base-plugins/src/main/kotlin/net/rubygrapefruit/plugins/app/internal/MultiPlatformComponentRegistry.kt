package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

open class MultiPlatformComponentRegistry(private val project: Project) {
    fun registerSourceSets(targets: ComponentTargets) {
        if (targets.nativeTargets.isEmpty()) {
            return
        }
        with(project.extensions.getByType(KotlinMultiplatformExtension::class.java)) {
            if (targets.jvm != null) {
                jvmToolchain {
                    it.languageVersion.set(targets.jvm.map { JavaLanguageVersion.of(it) })
                }
                jvm()
            }
            if (targets.browser) {
                js {
                    browser()
                }
            }

            val unixSourceSets = mutableSetOf<String>()
            val unixTestSourceSets = mutableSetOf<String>()

            if (targets.nativeTargets.contains(NativeMachine.MacOSX64)) {
                macosX64()
                unixSourceSets.add("macosMain")
                unixTestSourceSets.add("macosTest")
            }
            if (targets.nativeTargets.contains(NativeMachine.MacOSArm64)) {
                macosArm64()
                unixSourceSets.add("macosMain")
                unixTestSourceSets.add("macosTest")
            }
            if (targets.nativeTargets.contains(NativeMachine.LinuxX64)) {
                linuxX64()
                unixSourceSets.add("linuxMain")
                unixTestSourceSets.add("linuxTest")
            }
            if (targets.nativeTargets.contains(NativeMachine.WindowsX64)) {
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
}