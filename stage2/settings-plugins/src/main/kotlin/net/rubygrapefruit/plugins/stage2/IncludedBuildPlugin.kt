package net.rubygrapefruit.plugins.stage2

import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.initialization.Settings
import org.gradle.api.tasks.TaskContainer

@Suppress("unused")
class IncludedBuildPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            plugins.apply(BuildConstants.constants.stage1.plugins.includedBuild.id)
            gradle.rootProject { project ->
                project.run {
                    tasks.applyLifecycle("clean", target, project)
                    tasks.applyLifecycle("check", target, project)
                    tasks.applyLifecycle("assemble", target, project)
                    tasks.lifecycleTask("dist", target, project)
                    tasks.lifecycleTask("release", target, project)
                    tasks.lifecycleTask("docs", target, project)
                    tasks.lifecycleTask("samples", target, project)
                    tasks.applyLifecycle("verifySamples", target, project)
                    tasks.lifecycleTask("localSamples", target, project)
                }
            }
        }
    }

    private fun TaskContainer.lifecycleTask(name: String, target: Settings, project: Project) {
        register(name) {
            it.dependsOnChildren(name, target, project)
        }
    }

    private fun TaskContainer.applyLifecycle(name: String, target: Settings, project: Project) {
        named(name) {
            it.dependsOnChildren(name, target, project)
        }
    }

    private fun Task.dependsOnChildren(name: String, settings: Settings, rootProject: Project) {
        dependsOn(settings.gradle.includedBuilds.map { it.task(":${name}") })
        dependsOn(rootProject.subprojects.mapNotNull { it.tasks.findByName(name) })
    }
}