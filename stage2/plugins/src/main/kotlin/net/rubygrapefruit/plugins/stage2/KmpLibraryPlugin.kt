package net.rubygrapefruit.plugins.stage2

import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(BuildConstants.constants.stage0.buildConstants.pluginId)

            repositories.mavenCentral()

            group = BuildConstants.constants.production.libraries.group

            val jvmLibrary = objects.newInstance(JvmLibrary::class.java)
            val extension = extensions.create("library", KmpLibrary::class.java, jvmLibrary)

            extensions.getByType(KotlinMultiplatformExtension::class.java).apply {
                jvm()
                jvmToolchain {
                    it.languageVersion.set(extension.jvm.targetJvmVersion.map { JavaLanguageVersion.of(it) })
                }

                linuxX64()
                mingwX64()
                macosX64()
                macosArm64()

                js {
                    browser()
                }
            }
        }
    }
}