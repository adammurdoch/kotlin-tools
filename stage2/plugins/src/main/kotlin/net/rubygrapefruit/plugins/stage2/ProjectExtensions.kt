package net.rubygrapefruit.plugins.stage2

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.io.File

internal fun Project.applyKotlinSourceFromTargetProject(): Boolean {
    val targetFile = file("target.txt")
    if (!targetFile.exists()) {
        return false
    }

    val sourceDirProvider = provider {
        val targetProjectPath = targetFile.readText().trim()
        val targetProjectDir = file(targetProjectPath)
        if (!targetProjectDir.isDirectory) {
            throw IllegalArgumentException("Specified target project directory is not a directory: $targetProjectDir")
        }
        val sourceDir = file("$targetProjectDir/src/main/kotlin")
        if (sourceDir.exists()) {
            sourceDir
        } else {
            emptyList<File>()
        }
    }
    val kotlin = extensions.getByType(KotlinProjectExtension::class.java)
    kotlin.sourceSets.getByName("main").kotlin.srcDir(sourceDirProvider)

    return true
}