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
    abstract val mainMethod: Property<String>

    @get:Input
    abstract val delegateMethod: Property<String>

    @TaskAction
    fun generate() {
        sourceDirectory.file("${mainMethod.get()}.kt").get().asFile.printWriter().use {
            it.println(
                """
                fun ${mainMethod.get()}(args: Array<String>) {
                    ${delegateMethod.get()}(args)
                }
            """.trimIndent()
            )
        }
    }
}