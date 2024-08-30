package net.rubygrapefruit.plugins.docs.internal

import org.gradle.api.Plugin
import org.gradle.api.Project

open class DocsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            tasks.register("generateDocs", GenerateDocs::class.java) { task ->
                task.outputFile.set(project.layout.projectDirectory.file("README.md"))
            }
        }
    }
}