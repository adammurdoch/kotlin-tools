package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.NativeMachine
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
            val nativeSourceSets = mutableListOf<KotlinSourceSet>()
            val nativeTestSourceSets = mutableListOf<KotlinSourceSet>()
            val unixSourceSets = mutableListOf<KotlinSourceSet>()
            val unixTestSourceSets = mutableListOf<KotlinSourceSet>()
            val macosSourceSets = mutableListOf<KotlinSourceSet>()
            val macosTestSourceSets = mutableListOf<KotlinSourceSet>()

            if (targets.nativeTargets.contains(NativeMachine.MacOSX64)) {
                macosX64()
                macosSourceSets.add(sourceSets.getByName("macosX64Main"))
                macosTestSourceSets.add(sourceSets.getByName("macosX64Test"))
            }
            if (targets.nativeTargets.contains(NativeMachine.MacOSArm64)) {
                macosArm64()
                macosSourceSets.add(sourceSets.getByName("macosArm64Main"))
                macosTestSourceSets.add(sourceSets.getByName("macosArm64Test"))
            }
            if (targets.nativeTargets.contains(NativeMachine.LinuxX64)) {
                linuxX64()
                unixSourceSets.add(sourceSets.getByName("linuxX64Main"))
                unixTestSourceSets.add(sourceSets.getByName("linuxX64Test"))
            }
            if (targets.nativeTargets.contains(NativeMachine.WindowsX64)) {
                mingwX64()
                nativeSourceSets.add(sourceSets.getByName("mingwX64Main"))
                nativeTestSourceSets.add(sourceSets.getByName("mingwX64Test"))
            }
            val nativeMain = sourceSets.create("nativeMain") {
                it.dependsOn(sourceSets.getByName("commonMain"))
            }
            for (sourceSet in nativeSourceSets) {
                sourceSet.dependsOn(nativeMain)
            }

            // Some hacks to avoid duplicate symbol problem

            val commonTest = sourceSets.getByName("commonTest")
            val nativeTest = sourceSets.create("nativeTest") {
                it.dependsOn(commonTest)
            }
            for (sourceSet in nativeTestSourceSets) {
                sourceSet.dependsOn(nativeTest)
            }
            if (unixSourceSets.isNotEmpty() || macosSourceSets.isNotEmpty()) {
                val unixMain = sourceSets.create("unixMain") {
                    it.dependsOn(nativeMain)
                }
                for (sourceSet in unixSourceSets) {
                    sourceSet.dependsOn(unixMain)
                }
                val unixTest = sourceSets.create("unixTest") {
                    it.dependsOn(nativeTest)
                }
                for (sourceSet in unixTestSourceSets) {
                    sourceSet.dependsOn(unixTest)
                }
                if (macosSourceSets.isNotEmpty()) {
                    val macosMain = sourceSets.create("macosMain") {
                        it.dependsOn(unixMain)
                    }
                    for (sourceSet in macosSourceSets) {
                        sourceSet.dependsOn(macosMain)
                    }
                    val macosTest = sourceSets.create("macosTest") {
                        it.dependsOn(unixTest)
                    }
                    for (sourceSet in macosTestSourceSets) {
                        sourceSet.dependsOn(macosTest)
                    }
                }
            }
        }
    }
}