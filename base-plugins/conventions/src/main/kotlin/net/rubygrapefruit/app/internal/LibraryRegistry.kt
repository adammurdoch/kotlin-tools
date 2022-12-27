package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.NativeMachine
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

open class LibraryRegistry(private val project: Project) {
    private var hasLibrary = false

    fun registerLibrary(targets: ComponentTargets) {
        if (hasLibrary) {
            throw UnsupportedOperationException("Support for multiple libraries in the same project is not implemented.")
        }
        hasLibrary = true
        if (targets.nativeTargets.isEmpty()) {
            return
        }
        with(project.extensions.getByType(KotlinMultiplatformExtension::class.java)) {
            if (targets.jvm) {
                jvmToolchain(11)
                jvm()
            }
            val nativeSourceSets = mutableListOf<KotlinSourceSet>()
            val unixSourceSets = mutableListOf<KotlinSourceSet>()
            val macosSourceSets = mutableListOf<KotlinSourceSet>()

            if (targets.nativeTargets.contains(NativeMachine.MacOSX64)) {
                macosX64()
                macosSourceSets.add(sourceSets.getByName("macosX64Main"))
            }
            if (targets.nativeTargets.contains(NativeMachine.MacOSArm64)) {
                macosArm64()
                macosSourceSets.add(sourceSets.getByName("macosArm64Main"))
            }
            if (targets.nativeTargets.contains(NativeMachine.LinuxX64)) {
                linuxX64()
                unixSourceSets.add(sourceSets.getByName("linuxX64Main"))
            }
            if (targets.nativeTargets.contains(NativeMachine.WindowsX64)) {
                mingwX64()
                nativeSourceSets.add(sourceSets.getByName("mingwX64Main"))
            }
            unixSourceSets.addAll(macosSourceSets)
            nativeSourceSets.addAll(unixSourceSets)
            val nativeMain = sourceSets.create("nativeMain") {
                it.dependsOn(sourceSets.getByName("commonMain"))
            }
            for (sourceSet in nativeSourceSets) {
                sourceSet.dependsOn(nativeMain)
            }
            if (unixSourceSets.isNotEmpty()) {
                val unixMain = sourceSets.create("unixMain") {
                    it.dependsOn(nativeMain)
                }
                for (sourceSet in unixSourceSets) {
                    sourceSet.dependsOn(unixMain)
                }
                if (macosSourceSets.isNotEmpty()) {
                    val macosMain = sourceSets.create("macosMain") {
                        it.dependsOn(unixMain)
                    }
                    for (sourceSet in macosSourceSets) {
                        sourceSet.dependsOn(macosMain)
                    }
                }
            }
        }
    }
}