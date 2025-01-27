package net.rubygrapefruit.plugins.docs.internal

import net.rubygrapefruit.plugins.lifecycle.ComponentDetails
import net.rubygrapefruit.plugins.lifecycle.internal.LifecyclePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

open class DocsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(LifecyclePlugin::class.java)

            val componentModel = extensions.getByType(ComponentDetails::class.java)

            tasks.register("docs", GenerateDocs::class.java) { task ->
                task.sourceFiles.from(project.layout.projectDirectory.dir("src/docs").asFileTree)
                task.variables.put("project.version", componentModel.releaseCoordinates.map { it.version })
                task.variables.put("project.coordinates", componentModel.releaseCoordinates.map { it.formatted })
                task.outputFile.set(project.layout.projectDirectory.file("README.md"))
                task.outputDir.set(project.layout.projectDirectory.dir(".docs"))
            }
        }
    }
}