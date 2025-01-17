package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

abstract class UploadToMavenCentral : DefaultTask() {
    @get:Input
    abstract val groupId: Property<String>

    @get:Input
    abstract val artifactId: Property<String>

    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val userName: Property<String>

    @get:Input
    abstract val token: Property<String>

    @get:Internal
    abstract val repoDirectory: DirectoryProperty

    @get:Internal
    abstract val tempDirectory: DirectoryProperty

    @TaskAction
    fun upload() {
        val groupId = groupId.get()
        val artifactId = artifactId.get()
        val version = version.get()

        println("Uploading $groupId:$artifactId:$version")

        val repoDir = repoDirectory.get().asFile
        val moduleDir = repoDir.resolve("${groupId.replace('.', '/')}/$artifactId/$version")
        val files = moduleDir.listFiles().filter { it.isFile }
        println("Found ${files.size} files")

        val zipFile = tempDirectory.file("${artifactId}-${version}.zip").get().asFile
        zipFile.parentFile.mkdirs()
        zipFile.outputStream().use { outStream ->
            ZipOutputStream(outStream).use { zipStream ->
                for (file in files) {
                    zipStream.putNextEntry(ZipEntry(file.name))
                    file.inputStream().use { inStream ->
                        inStream.copyTo(zipStream)
                    }
                }
            }
        }
        println("Wrote $zipFile")

        val authToken = Base64.getEncoder().withoutPadding().encodeToString("${userName.get()}:${token.get()}".toByteArray())
        println("Auth token: $authToken")
    }
}