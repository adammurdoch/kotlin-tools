package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

abstract class GithubRelease : DefaultTask() {
    @get:Input
    abstract val tag: Property<String>

    @get:Input
    abstract val releaseName: Property<String>

    @get:Internal
    abstract val token: Property<String>

    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun release() {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.github.com/repos/adammurdoch/kotlin-tools/releases"))
            .header("Accept", "application/vnd.github+json")
            .header("Authorization", "Bearer ${token.get()}")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                {
                    "tag_name": "${tag.get()}",
                    "name": "${releaseName.get()}",
                    "draft": true,
                    "generate_release_notes": true
                }
            """.trimIndent()
                )
            )
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 201) {
            println("Status code: ${response.statusCode()}")
            println("Body: ${response.body()}")
            throw RuntimeException("Could not create Github release. Failed with status code ${response.statusCode()}")
        }
    }
}