package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.plugins.signing.SigningExtension

open class ReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            target.plugins.apply("maven-publish")
            target.plugins.apply("signing")

            val model = extensions.create("release", ReleaseExtension::class.java)
            model.nextVersion.convention("0.0.1-milestone-1")

            val effectiveVersion = model.nextVersion.map<VersionNumber> { v: String ->
                // Use a system property, because it is not possible to determine the version based on the presence of the `release` task in the graph when this project
                // is also used by a plugin (the jar for the JVM target is built at configuration time, when the `release` task is not scheduled)
                val version = VersionNumber.of(v)
                val releaseType = System.getProperty("release.type")
                when (releaseType) {
                    "final" -> version.final()
                    null -> version.milestone()
                    else -> throw IllegalArgumentException("Unknown release type: '$releaseType'")
                }
            }

            version = ProjectVersion(effectiveVersion)

            val releaseDir = layout.buildDirectory.dir("release")
            val zipDir = releaseDir.map { it.dir("zips") }

            // Maven repo does not use a lazy property, so use an eager values here
            val repoDir = releaseDir.map { it.dir("repo") }.get()

            val publishingModel = extensions.getByType(PublishingExtension::class.java)
            publishingModel.repositories {
                it.maven {
                    it.url = repoDir.asFile.toURI()
                }
            }

            val signingModel = extensions.getByType(SigningExtension::class.java)
            signingModel.useGpgCmd()
            signingModel.sign(publishingModel.publications)

            val mavenCentralUsername = providers.environmentVariable("MAVEN_CENTRAL_USERNAME")
            val mavenCentralToken = providers.environmentVariable("MAVEN_CENTRAL_TOKEN")

            val preTask = tasks.register("preRelease") { t ->
                t.doLast {
                    if (!(mavenCentralUsername.isPresent)) {
                        throw IllegalArgumentException("Please set 'MAVEN_CENTRAL_USERNAME' environment variable")
                    }
                    if (!(mavenCentralToken.isPresent)) {
                        throw IllegalArgumentException("Please set 'MAVEN_CENTRAL_TOKEN' environment variable")
                    }
                    println("Releasing version: ${project.version}")
                    repoDir.asFile.deleteRecursively()
                }
            }

            tasks.withType(PublishToMavenRepository::class.java).configureEach {
                it.mustRunAfter(preTask)
            }

            val uploadTasks = publishingModel.publications.withType(MavenPublication::class.java).map { p ->
                tasks.register("upload${p.name.capitalized()}", UploadToMavenCentral::class.java) { t ->
                    t.dependsOn("publish${p.name.capitalized()}PublicationToMavenRepository")
                    t.groupId.set(p.groupId)
                    t.artifactId.set(p.artifactId)
                    t.version.set(p.version)
                    t.userName.set(mavenCentralUsername)
                    t.token.set(mavenCentralToken)
                    t.repoDirectory.set(repoDir)
                    t.tempDirectory.set(zipDir)
                }
            }
            val uploadTask = tasks.register("upload") {
                it.dependsOn(uploadTasks)
            }

            val updateVersion = tasks.register("updateVersion", UpdateVersion::class.java) { t ->
                t.mustRunAfter(uploadTask)
                t.nextVersion.set(effectiveVersion.map { it.next() })
                t.buildFile.set(buildFile)
            }
            tasks.register("release") { t ->
                t.dependsOn(preTask)
                t.dependsOn(uploadTask)
                t.dependsOn(updateVersion)
                t.doLast {
                    println("Released version: ${project.version}")
                }
            }
        }
    }
}