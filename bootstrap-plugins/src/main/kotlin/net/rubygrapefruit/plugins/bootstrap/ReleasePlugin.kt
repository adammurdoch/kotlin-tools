package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project

open class ReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val release = extensions.create("release", ReleaseExtension::class.java)
            release.nextVersion.set("0.1")
            version = ProjectVersion(release.nextVersion)

            tasks.register("release") { t ->
                t.doLast {
                    println("Release version: ${project.version}")
                }
            }
        }
    }
}