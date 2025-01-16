package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class UploadToMavenCentral : DefaultTask() {
    @get:Input
    abstract val groupId: Property<String>

    @get:Input
    abstract val artifactId: Property<String>

    @get:Input
    abstract val version: Property<String>

    @get:Internal
    abstract val repoDirectory: DirectoryProperty

    @TaskAction
    fun upload() {
        val groupId = groupId.get()
        val artifactId = artifactId.get()
        val version = version.get()

        println("Uploading $groupId:$artifactId:$version")

        val repoDir = repoDirectory.get().asFile
        val moduleDir = repoDir.resolve("${groupId.replace('.', '/')}/$artifactId/$version")
        val files = moduleDir.listFiles().filter { it.isFile }
        for (file in files) {
            println("  $file")
        }
    }
}