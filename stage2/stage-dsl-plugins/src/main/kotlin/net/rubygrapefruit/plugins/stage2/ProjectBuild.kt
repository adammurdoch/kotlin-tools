package net.rubygrapefruit.plugins.stage2

import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.io.File
import kotlin.jvm.java

abstract class ProjectBuilder(private val settings: Settings) {
    internal abstract val projects: ListProperty<DowngradedProject>

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
            val sourceDirProvider = sourceDirProvider(spec, "main/kotlin")
            val kotlin = extensions.getByType(KotlinProjectExtension::class.java)
            kotlin.sourceSets.getByName("main").kotlin.srcDir(sourceDirProvider)
        }
        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val sourceDirProvider = sourceDirProvider(spec, "commonMain/kotlin")
            val kotlin = extensions.getByType(KotlinMultiplatformExtension::class.java)
            kotlin.sourceSets.getByName("commonMain").kotlin.srcDir(sourceDirProvider)
        }
        plugins.withId("java-library") {
            val sourceDirProvider = sourceDirProvider(spec, "main/java")
            val sourceSets = extensions.getByType(SourceSetContainer::class.java)
            sourceSets.getByName("main").java.srcDir(sourceDirProvider)
        }
        plugins.withType(JniLibraryPlugin::class.java) {
            val sourceDirProvider = sourceDirProvider(spec, "main/c")
            val lib = extensions.getByType(JniLibrary::class.java)
            lib.cSourceDirs.from(sourceDirProvider)
        }
        afterEvaluate {
            group = "stage3"
        }
    }

    private fun Project.sourceDirProvider(spec: DowngradedProject, path: String): Provider<Any> = provider {
        val sourceDir = spec.sourceProjectDir.resolve("src/$path")
        if (sourceDir.exists()) {
            sourceDir
        } else {
            emptyList<File>()
        }
    }
}

internal class DowngradedProject(val name: String, val projectDir: File, val sourceProjectDir: File) {
    val path: String get() = ":$name"
}