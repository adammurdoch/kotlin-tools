package net.rubygrapefruit.plugins.stage2

import org.gradle.api.initialization.Settings
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.io.File

abstract class ProjectBuilder(private val settings: Settings) {
    fun upgrade(path: String, name: String = path.substringAfterLast('/')) {
        downgrade(path, name)
    }

    fun downgrade(path: String, name: String = path.substringAfterLast('/')) {
        val settingsDir = settings.settingsDir
        val projectDir = settingsDir.resolve(path)
        val sourceProjectDir = settingsDir.resolve("../$path")
        add(DowngradedProject(name, projectDir, sourceProjectDir))
    }

    private fun add(spec: DowngradedProject) {
        settings.include(spec.name)
        val project = settings.project(spec.path)
        project.projectDir = spec.projectDir
        val sourceBuildScript = spec.sourceProjectDir.resolve("build.gradle.kts")
        project.buildFileName = sourceBuildScript.relativeTo(project.projectDir).path

        settings.gradle.rootProject { rootProject ->
            rootProject.project(spec.path) { p ->
                p.plugins.withId("org.jetbrains.kotlin.jvm") {
                    val sourceDirProvider = p.provider {
                        val sourceDir = spec.sourceProjectDir.resolve("src/main/kotlin")
                        if (sourceDir.exists()) {
                            sourceDir
                        } else {
                            emptyList<File>()
                        }
                    }
                    val kotlin = p.extensions.getByType(KotlinProjectExtension::class.java)
                    kotlin.sourceSets.getByName("main").kotlin.srcDir(sourceDirProvider)
                    p.afterEvaluate {
                        p.group = "stage3"
                    }
                }
            }
        }
    }
}

internal class DowngradedProject(val name: String, val projectDir: File, val sourceProjectDir: File) {
    val path: String get() = ":$name"
}