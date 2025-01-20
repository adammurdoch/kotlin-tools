package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.jvm.tasks.Jar
import org.gradle.plugins.signing.SigningExtension
import java.nio.file.Path
import kotlin.io.path.isDirectory

open class ReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            target.plugins.apply("maven-publish")
            target.plugins.apply("signing")

            val model = extensions.create("release", ReleaseExtension::class.java)
            model.nextVersion.convention("0.0.1-milestone-1")

            val effectiveVersion = model.nextVersion.map<VersionNumber> { v: String ->
                // Use a system property, because it is not possible to calculate the version based on the presence of the `release` task in the graph when this project
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

            tasks.withType(PublishToMavenRepository::class.java).configureEach { t ->
                t.mustRunAfter(preTask)
            }

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
            val uploadTasks = publications.map { p ->
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
            val uploadTask = tasks.register("upload") { t ->
                t.dependsOn(uploadTasks)
            }

            val tagName = effectiveVersion.map { "${project.name}-v${it}" }

            val tag = tasks.register("gitTag") { t ->
                t.mustRunAfter(uploadTask)
                t.doLast {
                    exec { e ->
                        e.commandLine("git", "tag", tagName.get())
                    }
                }
            }
            val githubRelease = tasks.register("githubRelease", GithubRelease::class.java) { t ->
                t.tag.set(tag.flatMap { tagName })
                t.releaseName.set(effectiveVersion.map { "${project.name.capitalized()} v${it}" })
                t.token.set(githubToken)
            }

            val updateVersion = tasks.register("updateVersion", UpdateVersion::class.java) { t ->
                t.mustRunAfter(tag)
                t.nextVersion.set(effectiveVersion.map { it.next() })
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