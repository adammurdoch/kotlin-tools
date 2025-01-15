package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project

open class ReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val release = extensions.create("release", ReleaseExtension::class.java)
            release.nextVersion.convention("0.1")

            val releaseTask = tasks.register("release") { t ->
                t.doLast {
                    println("Release version: ${project.version}")
                }
            }

            val effectiveVersion = release.nextVersion.map<String> { v: String ->
                if (gradle.taskGraph.hasTask(releaseTask.get())) {
                    v
                } else {
                    "$v-dev"
                }
            }

            version = ProjectVersion(effectiveVersion)
        }
    }
}