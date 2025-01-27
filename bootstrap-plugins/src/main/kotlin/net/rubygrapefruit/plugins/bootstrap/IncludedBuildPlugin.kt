package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.initialization.Settings

class IncludedBuildPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            gradle.rootProject { project ->
                project.run {
                    plugins.apply("lifecycle-base")
                    tasks.named("clean") {
                        it.dependsOnChildren("clean", target, project)
                    }
                    tasks.named("check") {
                        it.dependsOnChildren("check", target, project)
                    }
                    tasks.named("assemble") {
                        it.dependsOnChildren("assemble", target, project)
                    }
                    tasks.register("dist") {
                        it.dependsOnChildren("dist", target, project)
                    }
                    tasks.register("release") {
                        it.dependsOnChildren("release", target, project)
                    }
                    tasks.register("docs") {
                        it.dependsOnChildren("docs", target, project)
                    }
                    tasks.register("samples") {
                        it.dependsOnChildren("samples", target, project)
                    }
                    tasks.register("verifySamples") {
                        it.dependsOnChildren("verifySamples", target, project)
                    }
                }
            }
        }
    }

    private fun Task.dependsOnChildren(name: String, settings: Settings, rootProject: Project) {
        dependsOn(settings.gradle.includedBuilds.map { it.task(":${name}") })
        dependsOn(rootProject.subprojects.mapNotNull { it.tasks.findByName(name) })
    }
}