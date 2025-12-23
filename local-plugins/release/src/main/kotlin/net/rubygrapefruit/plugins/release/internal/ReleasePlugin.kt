package net.rubygrapefruit.plugins.release.internal

import net.rubygrapefruit.plugins.lifecycle.ComponentDetails
import net.rubygrapefruit.plugins.lifecycle.VersionNumber
import net.rubygrapefruit.plugins.lifecycle.internal.ComponentLifecyclePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.jvm.tasks.Jar
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.nio.file.Path
import kotlin.io.path.isDirectory

open class ReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            target.plugins.apply("maven-publish")
            target.plugins.apply("signing")
            target.plugins.apply(ComponentLifecyclePlugin::class.java)

            val model = extensions.getByType(ComponentDetails::class.java)

            // Use a system property, because it is not possible to calculate the version based on the presence of the `release` task in the graph when this project
            // is also used by a plugin (the jar for the JVM target is built at configuration time, when the `release` task is not scheduled)
            val releaseType = System.getProperty("release.type")
            when (releaseType) {
                "final" -> model.targetVersion.set(model.nextVersion.map { v -> VersionNumber.of(v).final() })
                null -> {} // ignore
                else -> throw IllegalArgumentException("Unknown release type: '$releaseType'")
            }
            val effectiveVersion = model.targetVersion

            val releaseDir = layout.buildDirectory.dir("release")

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
            val githubToken = providers.environmentVariable("GITHUB_TOKEN")

            val preTask = tasks.register("preRelease") { t ->
                t.doLast {
                    if (!(mavenCentralUsername.isPresent)) {
                        throw IllegalArgumentException("Please set the 'MAVEN_CENTRAL_USERNAME' environment variable")
                    }
                    if (!(mavenCentralToken.isPresent)) {
                        throw IllegalArgumentException("Please set the 'MAVEN_CENTRAL_TOKEN' environment variable")
                    }
                    if (!(githubToken.isPresent)) {
                        throw IllegalArgumentException("Please set the 'GITHUB_TOKEN' environment variable")
                    }
                    println("Releasing version: ${project.version}")
                    repoDir.asFile.deleteRecursively()
                }
            }

            val coordinates = tasks.register("outgoingCoordinates", ComponentCoordinates::class.java) { t ->
                t.coordinates.set(model.targetCoordinates)
                t.outputFile.set(layout.buildDirectory.file("component-coordinates.json"))
            }
            configurations.consumable("outgoingCoordinates") { c ->
                c.attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, "coordinates"))
                c.outgoing.artifact(coordinates)
            }
            val incoming = configurations.create("incomingCoordinates") { c ->
                c.isCanBeConsumed = false
                c.attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, "coordinates"))
            }
            tasks.register("incomingCoordinates", MergedCoordinates::class.java) { t ->
                val view = incoming.incoming.artifactView { v ->
                    v.lenient(true)
                }.files
                t.inputFiles.from(view)
                t.outputFile.set(layout.buildDirectory.file("incoming-coordinates.json"))
            }
            afterEvaluate {
                val kotlinModel = extensions.findByType(KotlinMultiplatformExtension::class.java)
                if (kotlinModel != null) {
                    for (target in kotlinModel.targets) {
                        val superConfig = configurations.findByName(target.runtimeElementsConfigurationName)
                        if (superConfig != null) {
                            incoming.extendsFrom(superConfig)
                        }
                    }
                }
            }

            tasks.withType(PublishToMavenRepository::class.java).configureEach { t ->
                t.mustRunAfter(preTask)
            }

            // Need a Javadoc jar for the JVM target
            val javadocJar = tasks.register("javadocJar", Jar::class.java) { t ->
                t.archiveClassifier.set("javadoc")
            }

            val publications = publishingModel.publications.withType(MavenPublication::class.java)
            val gitRepoDir = findGitRepoDir()
            publications.configureEach { p ->
                if (p.name == "jvm") {
                    p.artifact(javadocJar)
                }
                p.pom.run {
                    val pathToProject = gitRepoDir.relativize(projectDir.toPath())
                    description.set(model.description)
                    url.set("https://github.com/adammurdoch/kotlin-tools/tree/main/$pathToProject")
                    scm { s ->
                        s.connection.set("scm:git:https://github.com/adammurdoch/kotlin-tools")
                        s.url.set("https://github.com/adammurdoch/kotlin-tools")
                    }
                    licenses { l ->
                        l.license { license ->
                            license.name.set("The Apache License, Version 2.0")
                            license.url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers { d ->
                        d.developer { dev ->
                            dev.id.set("adam")
                            dev.name.set("Adam Murdoch")
                        }
                    }
                }
            }

            val uploadTask = tasks.register("upload")

            afterEvaluate {
                val publishTasks = publications.map { p -> "publish${p.name.capitalized()}PublicationToMavenRepository" }
                model.repository.builtBy(publishTasks)
                model.repository.from(repoDir)

                val uploadTasks = publications.map { p ->
                    tasks.register("upload${p.name.capitalized()}", UploadToMavenCentral::class.java) { t ->
                        t.dependsOn("publish${p.name.capitalized()}PublicationToMavenRepository")
                        t.groupId.set(p.groupId)
                        t.artifactId.set(p.artifactId)
                        t.version.set(p.version)
                        t.userName.set(mavenCentralUsername)
                        t.token.set(mavenCentralToken)
                        t.repoDirectory.set(repoDir)
                        t.tempDirectory.set(releaseDir.map { it.dir("upload") })
                    }
                }
                uploadTask.configure { t ->
                    t.dependsOn(uploadTasks)
                }
            }

            val tagName = effectiveVersion.map { "${project.name}-v${it}" }

            val tag = tasks.register("gitTag") { t ->
                t.mustRunAfter(uploadTask)
                t.doLast {
                    exec { e ->
                        e.commandLine("git", "tag", tagName.get())
                    }
                    exec { e ->
                        e.commandLine("git", "push", "origin", tagName.get())
                    }
                }
            }
            val githubRelease = tasks.register("githubRelease", GithubRelease::class.java) { t ->
                t.dependsOn(tag)
                t.tag.set(tagName)
                t.releaseName.set(effectiveVersion.map { "${project.name.capitalized()} v${it}" })
                t.prerelease.set(effectiveVersion.map { it.prerelease })
                t.token.set(githubToken)
            }

            val updateVersion = tasks.register("updateVersion", UpdateVersion::class.java) { t ->
                t.mustRunAfter(tag)
                t.nextVersion.set(effectiveVersion.map { it.nextMilestone() })
                t.buildFile.set(buildFile)
            }

            tasks.register("release") { t ->
                t.dependsOn(preTask)
                t.dependsOn(uploadTask)
                t.dependsOn(githubRelease)
                t.dependsOn(updateVersion)
                t.doLast {
                    println("Released version: ${project.version}")
                }
            }
        }
    }

    private fun Project.findGitRepoDir(): Path {
        var current = rootDir.toPath()
        while (current != null) {
            if (current.resolve(".git").isDirectory()) {
                return current
            }
            current = current.parent
        }
        throw IllegalStateException("Couldn't find the git repo root directory for $rootDir")
    }
}