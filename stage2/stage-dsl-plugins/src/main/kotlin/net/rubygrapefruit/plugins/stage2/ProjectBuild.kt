package net.rubygrapefruit.plugins.stage2

import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.io.File

abstract class ProjectBuilder(private val settings: Settings) {
    internal abstract val projects: ListProperty<DowngradedProject>

    fun downgrade(path: String, name: String = path.substringAfterLast('/')) {
        val settingsDir = settings.settingsDir
        val projectDir = settingsDir.resolve(path)
        val sourceProjectDir = settingsDir.resolve("../$path")
        add(DowngradedProject(name, projectDir, sourceProjectDir))
    }

    private fun add(spec: DowngradedProject) {
        projects.add(spec)
        settings.include(spec.name)
        val project = settings.project(spec.path)
        project.projectDir = spec.projectDir

        settings.gradle.rootProject { rootProject ->
            rootProject.project(spec.path) { project ->
                project.applySource(spec)
            }
        }
    }

    private fun Project.applySource(spec: DowngradedProject) {
        plugins.withId("org.jetbrains.kotlin.jvm") {
            val kotlin = extensions.getByType(KotlinProjectExtension::class.java)
            sourceDir(spec, "main", kotlin)
        }
        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val kotlin = extensions.getByType(KotlinMultiplatformExtension::class.java)
            sourceDir(spec, "commonMain", kotlin)
            sourceDir(spec, "jvmMain", kotlin)
        }
        plugins.withId("java-library") {
            val sourceDir = sourceDir(spec, "main/java")
            if (sourceDir != null) {
                val sourceSets = extensions.getByType(SourceSetContainer::class.java)
                sourceSets.getByName("main").java.srcDir(sourceDir)
            }
        }
        plugins.withType(JniLibraryPlugin::class.java) {
            val sourceDir = sourceDir(spec, "main/c")
            if (sourceDir != null) {
                val lib = extensions.getByType(JniLibrary::class.java)
                lib.cSourceDirs.from(sourceDir)
            }
        }
        afterEvaluate {
            group = "stage3"
        }
    }

    private fun Project.sourceDir(spec: DowngradedProject, sourceSetName: String, kotlin: KotlinProjectExtension) {
        val sourceDir = sourceDir(spec, "$sourceSetName/kotlin") ?: return
        afterEvaluate {
            kotlin.sourceSets.getByName(sourceSetName).kotlin.srcDir(sourceDir)
        }
    }

    private fun sourceDir(spec: DowngradedProject, path: String): File? {
        val sourceDir = spec.sourceProjectDir.resolve("src/$path")
        return if (sourceDir.exists()) {
            sourceDir
        } else {
            null
        }
    }
}

internal class DowngradedProject(val name: String, val projectDir: File, val sourceProjectDir: File) {
    val path: String get() = ":$name"
}