package net.rubygrapefruit.plugins.stage2

import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

@Suppress("unused")
class JvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(BuildConstants.constants.stage1.plugins.jvmBase.id)

            group = BuildConstants.constants.production.libraries.group

            val extension = extensions.create("library", JvmLibrary::class.java)
            extension.targetJvmVersion.convention(BuildConstants.constants.libs.jvm.version)

            val kotlin = extensions.getByType(KotlinProjectExtension::class.java)
            kotlin.jvmToolchain {
                it.languageVersion.set(extension.targetJvmVersion.map { JavaLanguageVersion.of(it) })
            }
        }
    }
}