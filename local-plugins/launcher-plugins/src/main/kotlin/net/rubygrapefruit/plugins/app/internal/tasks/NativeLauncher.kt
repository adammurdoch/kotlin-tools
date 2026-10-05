package net.rubygrapefruit.plugins.app.internal.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class NativeLauncher : DefaultTask() {
    @get:OutputDirectory
    abstract val sourceDirectory: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val delegateMethod: Property<String>

    @TaskAction
    fun generate() {
        val sourceDir = sourceDirectory.get().asFile
        sourceDir.deleteRecursively()
        sourceDir.mkdirs()
        sourceDir.resolve("Main.kt").printWriter().use {
            it.println(
                """
                // Generated file - do not edit
                package ${packageName.get()}
                   
                import net.rubygrapefruit.app.ui.launcher.runMain
 
                fun main(args: Array<String>) {
                    runMain {
                        ${delegateMethod.get()}(args)
                    }
                }
            """.trimIndent()
            )
        }
    }
}