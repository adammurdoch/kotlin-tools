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
    abstract val delegateClass: Property<String>

    @TaskAction
    fun generate() {
        sourceDirectory.file("main.kt").get().asFile.printWriter().use {
            it.println(
                """
                import platform.AppKit.NSApplication

                fun main() {
                    val application = NSApplication.sharedApplication
                    application.delegate = ${delegateClass.get()}()
                    application.run()
                }
            """.trimIndent()
            )
        }
    }
}