package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension

open class ReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            target.plugins.apply("maven-publish")
            val publishingModel = extensions.getByType(PublishingExtension::class.java)
            publishingModel.repositories {
                it.maven {
                    it.url = layout.buildDirectory.file("repo").get().asFile.toURI()
                }
            }

            val model = extensions.create("release", ReleaseExtension::class.java)
            model.nextVersion.convention("0.1")

            val releaseTask = tasks.register("release") { t ->
                t.dependsOn("publishAllPublicationsToMavenRepository")
                t.doLast {
                    println("Release version: ${project.version}")
                }
            }

            val effectiveVersion = model.nextVersion.map<VersionNumber> { v: String ->
                // Use a system property, because it is not possible to determine the version based on the presence of the `release` task in the graph when this project
                // is also used by a plugin (the jar for the JVM target is built at configuration time, when the `release` task is not scheduled)
                val version = VersionNumber(v)
                val releaseType = System.getProperty("release.type")
                when (releaseType) {
                    "final" -> version
                    null -> version.dev()
                    else -> throw IllegalArgumentException("Unknown release type: '$releaseType'")
                }
            }

            version = ProjectVersion(effectiveVersion)

            val updateTask = tasks.register("updateVersion", UpdateVersion::class.java) { t ->
                t.nextVersion.set(effectiveVersion.map { it.next() })
                t.buildFile.set(buildFile)
            }
            releaseTask.configure { t ->
                t.dependsOn(updateTask)
            }
        }
    }
}