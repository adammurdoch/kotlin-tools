package net.rubygrapefruit.plugins.docs.internal

import net.rubygrapefruit.plugins.lifecycle.ComponentDetails
import net.rubygrapefruit.plugins.lifecycle.internal.ComponentLifecyclePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

open class DocsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(ComponentLifecyclePlugin::class.java)

            val component = extensions.getByType(ComponentDetails::class.java)

            tasks.register("docs", GenerateDocs::class.java) { task ->
                task.sourceFiles.from(project.layout.projectDirectory.dir("src/docs").asFileTree)
                task.variables.put("component.version", component.releaseCoordinates.map { it.version })
                task.variables.put("component.coordinates", component.releaseCoordinates.map { it.formatted })
                task.outputFile.set(project.layout.projectDirectory.file("README.md"))
                task.outputDir.set(project.layout.projectDirectory.dir(".docs"))
            }
        }
    }
}