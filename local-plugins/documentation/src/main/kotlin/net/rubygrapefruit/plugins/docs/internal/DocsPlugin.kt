package net.rubygrapefruit.plugins.docs.internal

import org.gradle.api.Plugin
import org.gradle.api.Project

open class DocsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            tasks.register("generateDocs", GenerateDocs::class.java) { task ->
                task.sourceFiles.from(project.layout.projectDirectory.dir("src/docs").asFileTree)
                task.variables.put("project.version", provider { project.version.toString() })
                task.outputFile.set(project.layout.projectDirectory.file("README.md"))
                task.outputDir.set(project.layout.projectDirectory.dir(".docs"))
            }
        }
    }
}