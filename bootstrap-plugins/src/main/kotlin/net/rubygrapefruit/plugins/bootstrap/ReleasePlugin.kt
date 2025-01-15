package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project

open class ReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val model = extensions.create("release", ReleaseExtension::class.java)
            model.nextVersion.convention("0.1")

            val releaseTask = tasks.register("release") { t ->
                t.doLast {
                    println("Release version: ${project.version}")
                }
            }

            val effectiveVersion = model.nextVersion.map<VersionNumber> { v: String ->
                val version = VersionNumber(v)
                if (gradle.taskGraph.hasTask(releaseTask.get())) {
                    version
                } else {
                    version.dev()
                }
            }

            version = ProjectVersion(effectiveVersion)

            val updateTask = tasks.register("updateVersion", UpdateVersion::class.java) { t ->
                t.version.set(effectiveVersion.map { it.next() })
                t.buildFile.set(buildFile)
            }
            releaseTask.configure { t ->
                t.dependsOn(updateTask)
            }
        }
    }
}