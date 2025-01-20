package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.OutputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.createParentDirectories
import kotlin.io.path.outputStream

abstract class UploadToMavenCentral : DefaultTask() {
    @get:Input
    abstract val groupId: Property<String>

    @get:Input
    abstract val artifactId: Property<String>

    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val userName: Property<String>

    @get:Internal
    abstract val token: Property<String>

    @get:Internal
    abstract val repoDirectory: DirectoryProperty

    @get:Internal
    abstract val tempDirectory: DirectoryProperty

    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun upload() {
        val groupId = groupId.get()
        val artifactId = artifactId.get()
        val version = version.get()

        println("Uploading $groupId:$artifactId:$version")

        val repoDir = repoDirectory.get().asFile
        val moduleDir = repoDir.resolve("${groupId.replace('.', '/')}/$artifactId/$version")
        val files = moduleDir.listFiles().filter { it.isFile }

        val boundary = "*".repeat(10)

        val bodyFile = tempDirectory.file("${artifactId}-${version}.body").get().asFile.toPath()
        bodyFile.createParentDirectories()
        bodyFile.outputStream().use { outStream ->
            val builder = BodyBuilder(outStream, boundary)
            builder.file("bundle", "${artifactId}-${version}.zip") {
                val zipStream = ZipOutputStream(outStream)
                for (file in files) {
                    zipStream.putNextEntry(ZipEntry("/${groupId.replace('.', '/')}/${artifactId}/${version}/${file.name}"))
                    file.inputStream().use { inStream ->
                        inStream.copyTo(zipStream)
                    }
                }
                zipStream.finish()
            }
            builder.finish()
        }

        val authToken = Base64.getEncoder().withoutPadding().encodeToString("${userName.get()}:${token.get()}".toByteArray())

        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI("https://central.sonatype.com/api/v1/publisher/upload?publishingType=USER_MANAGED&name=$artifactId%20$version"))
            .header("Authorization", "Bearer $authToken")
            .header("Content-Type", "multipart/form-data, boundary=$boundary")
            .POST(HttpRequest.BodyPublishers.ofFile(bodyFile))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 201) {
            println("Status code: ${response.statusCode()}")
            println("Body: ${response.body()}")
            throw RuntimeException("Could not upload release to Maven Central. Failed with status code ${response.statusCode()}")
        }
    }

    private class BodyBuilder(val outStream: OutputStream, val boundary: String) {
        fun file(name: String, fileName: String, body: (OutputStream) -> Unit) {
            outStream.write("--".toByteArray())
            outStream.write(boundary.toByteArray())
            outStream.write("\r\n".toByteArray())
            outStream.write("""Content-Disposition: form-data; name="$name"; filename="$fileName"""".toByteArray())
            outStream.write("\r\n".toByteArray())
            outStream.write("Content-Type: application/octet-stream".toByteArray())
            outStream.write("\r\n".toByteArray())
            outStream.write("\r\n".toByteArray())
            body(outStream)
            outStream.write("\r\n".toByteArray())
        }

        fun finish() {
            outStream.write("--".toByteArray())
            outStream.write(boundary.toByteArray())
            outStream.write("--\r\n".toByteArray())
        }
    }
}