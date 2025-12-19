package net.rubygrapefruit.plugins.stage2

import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.io.File

@Suppress("unused")
class GradlePluginPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(BuildConstants.constants.stage1.plugins.gradlePlugin.id)

            val sourceDirProvider = provider {
                val targetFile = file("target.txt")
                if (targetFile.exists()) {
                    val targetProjectDir = targetFile.readText().trim()
                    val sourceDir = file("$targetProjectDir/src/main/kotlin")
                    require(sourceDir.isDirectory)
                    sourceDir
                } else {
                    emptyList<File>()
                }
            }
            val kotlin = extensions.getByType(KotlinProjectExtension::class.java)
            kotlin.sourceSets.getByName("main").kotlin.srcDir(sourceDirProvider)
        }
    }
}