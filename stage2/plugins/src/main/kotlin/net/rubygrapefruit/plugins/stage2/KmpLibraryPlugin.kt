package net.rubygrapefruit.plugins.stage2

import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("org.jetbrains.kotlin.multiplatform")

            repositories.mavenCentral()

            group = BuildConstants.constants.production.libraries.group

            extensions.getByType(KotlinMultiplatformExtension::class.java).apply {
                jvmToolchain(BuildConstants.constants.plugins.jvm.version)
                jvm()

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