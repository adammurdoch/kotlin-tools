package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.initialization.Settings
import org.gradle.api.tasks.TaskContainer

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
                    tasks.lifecycle("dist", target, project)
                    tasks.lifecycle("release", target, project)
                    tasks.lifecycle("docs", target, project)
                    tasks.lifecycle("samples", target, project)
                    tasks.lifecycle("verifySamples", target, project)
                    tasks.lifecycle("localSamples", target, project)
                }
            }
        }
    }

    private fun TaskContainer.lifecycle(name: String, target: Settings, project: Project) {
        register(name) {
            it.dependsOnChildren(name, target, project)
        }
    }

    private fun Task.dependsOnChildren(name: String, settings: Settings, rootProject: Project) {
        dependsOn(settings.gradle.includedBuilds.map { it.task(":${name}") })
        dependsOn(rootProject.subprojects.mapNotNull { it.tasks.findByName(name) })
    }
}